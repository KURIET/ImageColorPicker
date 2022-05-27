package com.bizzarestudy.imagecolorpicker

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bizzarestudy.imagecolorpicker.presentation.pixels.PixelViewModel

class PixelActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pixel)

        val model: PixelViewModel by viewModels()
        val uri = intent.getParcelableExtra<Uri>("imageUrl")
        val colors = model.getPixelColors(applicationContext, uri)
        colors.forEach { Log.i("KM-01", "A: ${it.a}, R: ${it.r}, G: ${it.g}, B: ${it.b}") }
    }
}