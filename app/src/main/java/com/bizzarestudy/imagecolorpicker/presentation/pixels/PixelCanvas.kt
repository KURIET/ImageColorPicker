package com.bizzarestudy.imagecolorpicker.presentation.pixels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.bizzarestudy.imagecolorpicker.domain.model.PixelColor

open class PixelCanvas @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var pixel: Double = 1.0
    private var xCount: Int = 0
    private var yCount: Int = 0

    private var colors = arrayListOf<PixelColor>()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (colors.isEmpty()) {
            return
        }

        val screenWidth = resources.displayMetrics.widthPixels + 0.0
        val screenHeight = resources.displayMetrics.heightPixels + 0.0

        pixel = if (screenWidth < screenHeight) {
            screenWidth / xCount - 1
        } else {
            screenHeight / yCount - 1
        }

        while (true) {
            pixel += 0.00001
            if (pixel * xCount > screenWidth || pixel * yCount > screenHeight) {
                pixel -= 0.00001
                break
            }
        }

        Log.i("KM-01", "pixel: $pixel")

        for (y in 0 until yCount) {
            for (x in 0 until xCount) {
                val cur = colors[y * xCount + x]
                val paint = Paint().apply {
                    color = Color.argb(cur.a, cur.r, cur.g, cur.b)
                    isAntiAlias = true
                    style = Paint.Style.FILL
                }
                val rect = RectF().apply {
                    left = (x.toDouble() * pixel).toFloat()
                    top = (y.toDouble() * pixel).toFloat()
                    right = (x.toDouble() * pixel + pixel).toFloat()
                    bottom = (y.toDouble() * pixel + pixel).toFloat()
                }
                canvas?.drawRect(rect, paint)
            }
        }
    }

    fun set(pixelColorList: ArrayList<PixelColor>, pixelWidth: Int, pixelHeight: Int) {
        xCount = pixelWidth
        yCount = pixelHeight
        colors = pixelColorList
        invalidate()
        requestLayout()
    }
}