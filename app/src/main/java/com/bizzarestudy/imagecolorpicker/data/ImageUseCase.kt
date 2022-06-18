package com.bizzarestudy.imagecolorpicker.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.bizzarestudy.imagecolorpicker.domain.model.PixelColor
import com.bizzarestudy.imagecolorpicker.presentation.pixels.PixelCanvas
import kotlin.math.roundToInt

object ImageUseCase {

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

    fun drawPixels(
        context: Context,
        bitmap: Bitmap,
        xCount: Int,
        yCount: Int,
        pixel: Double,
        selectedPixel: Int
    ): Bitmap {
        val bitmapToSave = Bitmap.createBitmap(
            (xCount * pixel).roundToInt(),
            (yCount * pixel).roundToInt(), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmapToSave)
        val pixelCanvas = PixelCanvas(context)

        // colors 가상 캔버스에 세팅
        pixelCanvas.set(getColorsFrom(bitmap, selectedPixel), xCount, yCount)

        // 가상 캔버스에 픽셀 이미지 그리기
        pixelCanvas.draw(canvas)
        return bitmapToSave
    }

    private fun getColorsFrom(bitmap: Bitmap, selectedPixel: Int): ArrayList<PixelColor> {
        val xCount = selectedPixel - 1
        val yCount = getCustomHeight(bitmap, xCount)
        val colors = arrayListOf<PixelColor>()
        val chunk = bitmap.width / (xCount + 1)

        for (y in 0 until yCount) {
            for (x in 0 until xCount) {
                val pixelColor = bitmap.getPixel(x * chunk, y * chunk)
                colors.add(abgrToColor(pixelColor))
            }
        }
        return colors
    }

    fun getCustomHeight(bitmap: Bitmap, pixel: Int): Int {
        return (pixel * (bitmap.height.toDouble() / bitmap.width)).toInt()
    }
}