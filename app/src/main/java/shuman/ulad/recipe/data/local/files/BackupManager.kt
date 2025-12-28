package shuman.ulad.recipe.data.local.files

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shuman.ulad.recipe.data.local.RecipeDao
import shuman.ulad.recipe.data.local.entity.RecipeEntity
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

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: RecipeDao,
    private val imageStorageManager: ImageStorageManager
) {
    private val gson = Gson()

    suspend fun exportBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val recipes = dao.getAllRecipesOneShot()

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
            var jsonString: String? = null

            while (entry != null) {
                val name = entry.name

                if (name == "data.json") {
                    jsonString = zipInputStream.bufferedReader().readText()
                } else if (name.startsWith("images/")) {
                    val fileName = File(name).name
                    val targetFile = File(imageDirectory, fileName)
                    FileOutputStream(targetFile).use { outputStream ->
                        zipInputStream.copyTo(outputStream)
                    }
                }

                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }

            if (jsonString != null) {
                val type = object : TypeToken<List<RecipeEntity>>() {}.type
                val rawRecipes : List<RecipeEntity> = gson.fromJson(jsonString, type)

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
        }
    }
}