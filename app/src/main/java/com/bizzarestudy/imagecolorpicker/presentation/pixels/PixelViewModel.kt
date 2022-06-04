package com.bizzarestudy.imagecolorpicker.presentation.pixels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bizzarestudy.imagecolorpicker.R
import com.bizzarestudy.imagecolorpicker.data.GetDivisors
import com.bizzarestudy.imagecolorpicker.data.ImageUseCase
import com.bizzarestudy.imagecolorpicker.data.PixelWidth
import com.bizzarestudy.imagecolorpicker.domain.model.PixelColor
import com.bizzarestudy.imagecolorpicker.util.Share
import kotlin.math.roundToInt

class PixelViewModel constructor(application: Application) : AndroidViewModel(application) {

    var colors: ArrayList<PixelColor> = arrayListOf()
    private lateinit var bitmap: Bitmap
    private lateinit var fileSegName: String
    var pixelHeight: Int = 1
    var pixelWidth: Int = 1
    private var pixelWidthList: ArrayList<Int> = arrayListOf()
    private var pixelIndex = 2

    fun getFirstColorList(context: Context, uri: Uri?): ArrayList<PixelColor> {
        val tempBitmap = ImageUseCase.getBitmapFromUri(context, uri)
        fileSegName = getNameFrom(uri)

        bitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true)
        pixelWidthList = GetDivisors.by(bitmap.width)
        pixelWidth = pixelWidthList[pixelIndex] + 1
        return getColors(pixelWidth)
    }

    private fun getNameFrom(uri: Uri?) = uri?.pathSegments!![uri.pathSegments!!.size - 1]

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

        val context = getContext()
        val bitmapToSave = drawVirtualPixels(context)

        return try {
            ImageUseCase.saveBitmap(context, bitmapToSave, fileSegName)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shareImage() {
        val context = getContext()
        val share = Share(context)
        val bitmapToSave = drawVirtualPixels(context)
        val uri = ImageUseCase.saveBitmap(context, bitmapToSave, fileSegName)
        share.shareFile(uri, "text", "subject")
    }

    private fun drawVirtualPixels(context: Context): Bitmap {
        val pixel = PixelWidth.get(
            context.resources,
            pixelWidth,
            pixelHeight
        )
        val bitmapToSave = Bitmap.createBitmap(
            (pixelWidth * pixel).roundToInt(),
            (pixelHeight * pixel).roundToInt(), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmapToSave)
        val pixelCanvas = PixelCanvas(context)
        pixelCanvas.set(getColors(pixelWidthList[pixelIndex]), pixelWidth, pixelHeight)
        pixelCanvas.draw(canvas)
        return bitmapToSave
    }

    private fun getContext(): Context {
        return getApplication<Application>().applicationContext
    }

}