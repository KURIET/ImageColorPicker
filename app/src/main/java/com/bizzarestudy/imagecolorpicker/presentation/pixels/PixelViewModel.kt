package com.bizzarestudy.imagecolorpicker.presentation.pixels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.bizzarestudy.imagecolorpicker.domain.model.PixelColor

class PixelViewModel(application: Application) : AndroidViewModel(application) {

    fun getPixelColors(context: Context, uri: Uri?): ArrayList<PixelColor> {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri!!)
            ImageDecoder.decodeBitmap(source)
        }
        val width = bitmap.width
        val height = bitmap.height
        val colors = arrayListOf<PixelColor>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val pixelColor = softwareBitmap.getPixel(x, y)
                colors.add(abgrToColor(pixelColor))
            }
        }
        return colors
    }

    private fun abgrToColor(argbColor: Int): PixelColor {
        val a: Int = argbColor shr 24 and 0xff // or color >>> 24
        val r: Int = argbColor shr 16 and 0xff
        val g: Int = argbColor shr 8 and 0xff
        val b: Int = argbColor and 0xff
        return PixelColor(a, r, g, b)
    }

}