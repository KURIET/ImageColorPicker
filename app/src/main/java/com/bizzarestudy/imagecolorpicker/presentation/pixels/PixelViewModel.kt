package com.bizzarestudy.imagecolorpicker.presentation.pixels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bizzarestudy.imagecolorpicker.domain.model.PixelColor

class PixelViewModel constructor(application: Application) : AndroidViewModel(application) {

    private lateinit var bitmap: Bitmap
    var pixelHeight: Int = 1
    var pixelWidth: Int = 1
    private var pixelWidthList: ArrayList<Int> = arrayListOf()
    private var pixelIndex = 0

    fun getPixelColorList(context: Context, uri: Uri?): ArrayList<PixelColor> {
        bitmap = getBitmapFromUri(context, uri)
        pixelWidthList = by(bitmap.width)
        pixelWidth = pixelWidthList[0]
        return getColors(pixelWidth)
    }

    private fun getColors(pixelWidth: Int): ArrayList<PixelColor> {
        val imageWidth = bitmap.width
        pixelHeight = getCustomHeight(bitmap, pixelWidth)
        val colors = arrayListOf<PixelColor>()
        val chunk = imageWidth / (pixelWidth + 1)

        Log.i("KM-01", "pixelHeight: $pixelHeight, pixelWidth: $pixelWidth")

        for (y in 0 until pixelHeight) {
            for (x in 0 until pixelWidth) {
                val soft = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val pixelColor = soft.getPixel(x * chunk, y * chunk)
                colors.add(abgrToColor(pixelColor))
            }
        }
        return colors
    }

    private fun getCustomHeight(bitmap: Bitmap, pixel: Int): Int {
        return pixel * (bitmap.height / bitmap.width)
    }

    private fun by(num: Int): ArrayList<Int> {
        val mid = num / 2
        val result = arrayListOf<Int>()
        for (i in 3..mid) {
            if (num % i == 0 && i <= limitSize) {
                result.add(i - 1)
            }
        }
        return result
    }

    private fun getBitmapFromUri(
        context: Context,
        uri: Uri?
    ) = if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri!!)
        ImageDecoder.decodeBitmap(source)
    }

    private fun abgrToColor(argbColor: Int): PixelColor {
        val a: Int = argbColor shr 24 and 0xff // or color >>> 24
        val r: Int = argbColor shr 16 and 0xff
        val g: Int = argbColor shr 8 and 0xff
        val b: Int = argbColor and 0xff
        return PixelColor(a, r, g, b)
    }

    fun hasNextString(): String {
        return if (hasNext()) "Next" else "Last"
    }

    fun hasBeforeString(): String {
        return if (hasBefore()) "Prev" else "First"
    }

    fun showNext() {
        if (hasNext()) {
            getColors(pixelWidthList[++pixelIndex])
        }
    }

    fun showBefore() {
        if (hasBefore()) {
            getColors(pixelWidthList[--pixelIndex])
        }
    }

    fun hasNext(): Boolean {
        return pixelIndex + 1 < pixelWidthList.size
    }

    fun hasBefore(): Boolean {
        return 2 < pixelIndex
    }

    companion object {
        const val limitSize: Int = 500
    }

}