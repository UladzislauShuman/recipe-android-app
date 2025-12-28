package shuman.ulad.recipe.data.local.files

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun saveImage(contentUri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(contentUri)
            ?: throw Exception("Cannot open image input stream")

        val fileName = "${UUID.randomUUID()}.jpg"

        val directory = File(context.filesDir, "images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)

        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return@withContext file.absolutePath
    }
}