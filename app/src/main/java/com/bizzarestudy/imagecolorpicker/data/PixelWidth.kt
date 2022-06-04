package com.bizzarestudy.imagecolorpicker.data

import android.content.res.Resources
import android.util.Log

object PixelWidth {
    fun get(
        resources: Resources,
        xCount: Int,
        yCount: Int
    ): Double {

        val density = resources.displayMetrics.density
        val screenWidth = resources.displayMetrics.widthPixels.toFloat() / density + 0.0
        val screenHeight = resources.displayMetrics.heightPixels.toFloat() / density + 0.0

        var pixel = if (screenWidth < screenHeight) {
            screenWidth / xCount - 1
        } else {
            screenHeight / yCount - 1
        }

        Log.i("KM-01", "screenWidth: $screenWidth, screenHeight: $screenHeight, pixel: $pixel, density: $density")

        while (true) {
            pixel += 0.0001
            if (pixel * xCount > screenWidth || pixel * yCount > screenHeight) {
                pixel -= 0.0001
                pixel *= density
                break
            }
        }
        return pixel
    }
}