package com.example.myandroidapp.inception25.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {

    suspend fun saveImageToPrivateStorage(context: Context, uri: Uri): String? {
        return try {
            withContext(Dispatchers.IO) {
                //  уникальное имя файла
                val fileName = "plant_${UUID.randomUUID()}.jpg"

                // папка для сохранения файлов приложения
                val storageDir = context.filesDir
                val outputFile = File(storageDir, "plant_images").apply {
                    if (!exists()) mkdirs()
                }

                val outputFileFinal = File(outputFile, fileName)

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(outputFileFinal).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                //  относительный путь
                "plant_images/$fileName"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getImageFile(context: Context, relativePath: String): File {
        return File(context.filesDir, relativePath)
    }

    fun getImageUri(context: Context, relativePath: String): Uri {
        val file = getImageFile(context, relativePath)
        return Uri.fromFile(file)
    }
}