package shuman.ulad.recipe.data.local.files

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shuman.ulad.recipe.data.local.RecipeDao
import shuman.ulad.recipe.data.local.entity.RecipeEntity
import shuman.ulad.recipe.data.local.preferences.AppPreferences
import shuman.ulad.recipe.data.local.preferences.AppTheme
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class BackupSettingsDto(
    val theme: String,
    val languageCode: String?
)

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: RecipeDao,
    private val appPreferences: AppPreferences
) {
    private val gson = Gson()

    suspend fun exportBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val recipes = dao.getAllRecipesOneShot()

        val currentTheme = appPreferences.getThemeSnapshot()
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentLanguage = if (!currentLocales.isEmpty) currentLocales.get(0)?.language else null

        val settingsDto = BackupSettingsDto(
            theme = currentTheme.name,
            languageCode = currentLanguage
        )

        val outputStream = context.contentResolver.openOutputStream(uri)
            ?: throw Exception("Cannot open output stream")

        ZipOutputStream(BufferedOutputStream(outputStream)).use {
            zipOutputStream ->
            val portableRecipes = recipes.map {
                entity ->
                if (entity.isLocal && entity.imageUrl.isNotBlank()) {
                    val fileName = File(entity.imageUrl).name
                    entity.copy(imageUrl = fileName)
                } else {
                    entity
                }
            }
            val jsonString = gson.toJson(portableRecipes)
            val jsonEntry = ZipEntry("data.json")
            zipOutputStream.putNextEntry(jsonEntry)
            zipOutputStream.write(jsonString.toByteArray())
            zipOutputStream.closeEntry()

            val settingsJson = gson.toJson(settingsDto)
            val settingsEntry = ZipEntry("settings.json")
            zipOutputStream.putNextEntry(settingsEntry)
            zipOutputStream.write(settingsJson.toByteArray())
            zipOutputStream.closeEntry()

            recipes
                .filter {
                    it.isLocal && it.imageUrl.isNotBlank()
                }
                .forEach { entity ->
                    val file = File(entity.imageUrl)
                    if (file.exists()) {
                        val imageEntry = ZipEntry("images/${file.name}")
                        zipOutputStream.putNextEntry(imageEntry)

                        FileInputStream(file).use { input ->
                            input.copyTo(zipOutputStream)
                        }
                        zipOutputStream.closeEntry()
                    }
                }
        }
    }

    suspend fun importBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open input stream")

        val restoreRecipes = mutableListOf<RecipeEntity>()
        val imageDirectory = File(context.filesDir, "images")
        if (!imageDirectory.exists()) imageDirectory.mkdirs()

        ZipInputStream(BufferedInputStream(inputStream)).use {
            zipInputStream ->
            var entry = zipInputStream.nextEntry
            var dataJsonString: String? = null
            var settingsJsonString: String? = null

            while (entry != null) {
                val name = entry.name

                when {
                    name == "data.json" -> {
                        dataJsonString = zipInputStream.bufferedReader().readText()
                    }
                    name == "settings.json" -> {
                        settingsJsonString = zipInputStream.bufferedReader().readText()
                    }
                    name.startsWith("images/") -> {
                        val fileName = File(name).name
                        val targetFile = File(imageDirectory, fileName)
                        FileOutputStream(targetFile).use { outputStream ->
                            zipInputStream.copyTo(outputStream)
                        }
                    }
                }

                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }

            if (dataJsonString != null) {
                val type = object : TypeToken<List<RecipeEntity>>() {}.type
                val rawRecipes : List<RecipeEntity> = gson.fromJson(dataJsonString, type)

                val fixedRecipes = rawRecipes.map { entity ->
                    if (entity.isLocal && entity.imageUrl.isNotBlank()) {
                        val fullPath = File(imageDirectory, entity.imageUrl).absolutePath
                        entity.copy(imageUrl = fullPath)
                    } else {
                        entity
                    }
                }

                dao.insertAll(fixedRecipes)
            }

            if (settingsJsonString != null) {
                try {
                    val settings = gson.fromJson(settingsJsonString, BackupSettingsDto::class.java)
                    val theme = AppTheme.valueOf(settings.theme)
                    appPreferences.setTheme(theme)

                    withContext(Dispatchers.Main) {
                        if (settings.languageCode != null) {
                            val appLocale = LocaleListCompat.forLanguageTags(settings.languageCode)
                            AppCompatDelegate.setApplicationLocales(appLocale)
                        } else {
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}