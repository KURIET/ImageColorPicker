package com.bizzarestudy.imagecolorpicker.presentation.pixels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bizzarestudy.imagecolorpicker.R
import com.bizzarestudy.imagecolorpicker.data.GetDivisors
import com.bizzarestudy.imagecolorpicker.data.ImageUseCase
import com.bizzarestudy.imagecolorpicker.data.PixelWidth
import com.bizzarestudy.imagecolorpicker.data.SaveUseCase
import com.bizzarestudy.imagecolorpicker.domain.model.PixelColor
import com.bizzarestudy.imagecolorpicker.util.Share

class PixelViewModel constructor(application: Application) : AndroidViewModel(application) {

    var colors: ArrayList<PixelColor> = arrayListOf()
    private lateinit var bitmap: Bitmap
    private lateinit var fileSegName: String
    var yCount: Int = 1
    var xCount: Int = 1
    private var xCountList: ArrayList<Int> = arrayListOf()
    private var pixelIndex = 2

    fun getFirstColorList(context: Context, uri: Uri?): ArrayList<PixelColor> {
        val tempBitmap = ImageUseCase.getBitmapFromUri(context, uri)
        fileSegName = getNameFrom(uri)

        bitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true)
        xCountList = GetDivisors.by(bitmap.width)
        xCount = xCountList[pixelIndex] + 1
        return getColorsFrom(xCount)
    }

    private fun getNameFrom(uri: Uri?) = uri?.pathSegments!![uri.pathSegments!!.size - 1]

    private fun getColorsFrom(selectedPixel: Int): ArrayList<PixelColor> {
        val imageWidth = bitmap.width
        xCount = selectedPixel - 1
        yCount = ImageUseCase.getCustomHeight(bitmap, xCount)
        colors = arrayListOf()
        val chunk = imageWidth / (xCount + 1)
        Log.d("KM-01", "pixelHeight: $yCount, pixelWidth: $xCount")
        for (y in 0 until yCount) {
            for (x in 0 until xCount) {
                val pixelColor = bitmap.getPixel(x * chunk, y * chunk)
                colors.add(ImageUseCase.abgrToColor(pixelColor))
            }
        }
        return colors
    }

    fun getNextString(): String {
        return if (hasNext()) getString(R.string.nextButton) else getString(R.string.lastButton)
    }

    fun getBeforeString(): String {
        return if (hasBefore()) getString(R.string.prevButton) else getString(R.string.firstButton)
    }

    private fun getString(id: Int): String {
        return getContext().resources.getString(id)
    }

    fun showNext(): Boolean {
        if (hasNext()) {
            getColorsFrom(xCountList[++pixelIndex])
            return true
        }
        return false
    }

    fun showBefore(): Boolean {
        if (hasBefore()) {
            getColorsFrom(xCountList[--pixelIndex])
            return true
        }
        return false
    }

    private fun hasNext(): Boolean {
        return pixelIndex + 1 < xCountList.size
    }

    private fun hasBefore(): Boolean {
        return 2 < pixelIndex
    }

    fun saveImage(): Boolean {
        val context = getContext()
        val bitmapToSave: Bitmap = drawVirtualPixels(context)

        return try {
            SaveUseCase.saveBitmap(context, bitmapToSave, fileSegName)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shareImage() {
        val context = getContext()
        val bitmapToSave = drawVirtualPixels(context)
        val uri = SaveUseCase.saveBitmap(context, bitmapToSave, fileSegName)

        Share(context).shareFile(uri, "text", "subject")
    }

    private fun drawVirtualPixels(context: Context): Bitmap {
        val pixel = PixelWidth.get(context.resources, xCount, yCount)
        val selectedPixel = xCountList[pixelIndex]
        return ImageUseCase.drawPixels(context, bitmap, xCount, yCount, pixel, selectedPixel)
    }

    private fun getContext(): Context {
        return getApplication<Application>().applicationContext
    }

}