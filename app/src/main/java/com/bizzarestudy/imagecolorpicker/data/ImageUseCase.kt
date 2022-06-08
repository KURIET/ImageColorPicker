package com.bizzarestudy.imagecolorpicker.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.bizzarestudy.imagecolorpicker.domain.model.PixelColor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object ImageUseCase {

    @Throws(IOException::class)
    fun saveBitmap(
        context: Context, bitmap: Bitmap, fileSegName: String
    ): Uri {

        val displayName = "${fileSegName}_pixel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // https://stackoverflow.com/questions/56904485/how-to-save-an-image-in-android-q-using-mediastore
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }

            var uri: Uri? = null

            return runCatching {
                with(context.contentResolver) {
                    insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.also {
                        uri = it // Keep uri reference so it can be removed on failure

                        openOutputStream(it)?.use { stream ->
                            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
                                throw IOException("Failed to save bitmap.")
                        } ?: throw IOException("Failed to open output stream.")

                    } ?: throw IOException("Failed to create new MediaStore record.")
                }
            }.getOrElse {
                uri?.let { orphanUri ->
                    // Don't leave an orphan entry in the MediaStore
                    context.contentResolver.delete(orphanUri, null, null)
                }

                throw it
            }
        } else {
            @Suppress("DEPRECATION")
            val root = Environment.getExternalStorageDirectory().toString() + "/color_picker/"
            val myDir = File(root).apply { mkdirs() }
            val file = File(myDir, "$displayName.png")
            if (file.exists()) {
                file.delete()
            }
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file.toUri()
        }
    }

    private fun getRandom(): Int {
        val random = Random(1000)
        return random.nextInt(1000) + 1
    }

    fun abgrToColor(argbColor: Int): PixelColor {
        val a: Int = argbColor shr 24 and 0xff // or color >>> 24
        val r: Int = argbColor shr 16 and 0xff
        val g: Int = argbColor shr 8 and 0xff
        val b: Int = argbColor and 0xff
        return PixelColor(a, r, g, b)
    }

    fun getBitmapFromUri(
        context: Context,
        uri: Uri?
    ): Bitmap = if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri!!)
        ImageDecoder.decodeBitmap(source)
    }
}