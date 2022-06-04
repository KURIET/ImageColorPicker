package com.bizzarestudy.imagecolorpicker.presentation.pixels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bizzarestudy.imagecolorpicker.data.GetDivisors
import com.bizzarestudy.imagecolorpicker.data.ImageUseCase
import com.bizzarestudy.imagecolorpicker.domain.model.PixelColor
import com.bizzarestudy.imagecolorpicker.util.Share
import java.lang.Exception

class PixelViewModel constructor(application: Application) : AndroidViewModel(application) {

    var colors: ArrayList<PixelColor> = arrayListOf()
    private lateinit var bitmap: Bitmap
    var pixelHeight: Int = 1
    var pixelWidth: Int = 1
    private var pixelWidthList: ArrayList<Int> = arrayListOf()
    private var pixelIndex = 2

    fun getFirstColorList(context: Context, uri: Uri?): ArrayList<PixelColor> {
        val tempBitmap = ImageUseCase.getBitmapFromUri(context, uri)
        bitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true)
        pixelWidthList = GetDivisors.by(bitmap.width)
        pixelWidth = pixelWidthList[pixelIndex] + 1
        return getColors(pixelWidth)
    }

    private fun getColors(selectedPixel: Int): ArrayList<PixelColor> {
        val imageWidth = bitmap.width
        pixelWidth = selectedPixel - 1
        pixelHeight = getCustomHeight(bitmap, pixelWidth)
        colors = arrayListOf()
        val chunk = imageWidth / (pixelWidth + 1)
        Log.i("KM-01", "pixelHeight: $pixelHeight, pixelWidth: $pixelWidth")
        for (y in 0 until pixelHeight) {
            for (x in 0 until pixelWidth) {
                val pixelColor = bitmap.getPixel(x * chunk, y * chunk)
                colors.add(ImageUseCase.abgrToColor(pixelColor))
            }
        }
        return colors
    }

    private fun getCustomHeight(bitmap: Bitmap, pixel: Int): Int {
        return (pixel * (bitmap.height.toDouble() / bitmap.width)).toInt()
    }

    fun getNextString(): String {
        return if (hasNext()) "Next" else "Last"
    }

    fun getBeforeString(): String {
        return if (hasBefore()) "Prev" else "First"
    }

    fun showNext(): Boolean {
        if (hasNext()) {
            getColors(pixelWidthList[++pixelIndex])
            return true
        }
        return false
    }

    fun showBefore(): Boolean {
        if (hasBefore()) {
            getColors(pixelWidthList[--pixelIndex])
            return true
        }
        return false
    }

    private fun hasNext(): Boolean {
        return pixelIndex + 1 < pixelWidthList.size
    }

    private fun hasBefore(): Boolean {
        return 2 < pixelIndex
    }

    fun saveImage(): Boolean {
        return try {
            ImageUseCase.saveBitmap(getApplication<Application>().applicationContext, bitmap)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shareImage() {
        val share = Share(getApplication<Application>().applicationContext)
        val uri = ImageUseCase.saveBitmap(getApplication<Application>().applicationContext, bitmap)
        share.shareFile(uri, "text", "subject")
    }



}