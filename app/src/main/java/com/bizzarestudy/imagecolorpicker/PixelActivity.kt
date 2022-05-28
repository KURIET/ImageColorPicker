package com.bizzarestudy.imagecolorpicker

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bizzarestudy.imagecolorpicker.databinding.ActivityPixelBinding
import com.bizzarestudy.imagecolorpicker.presentation.pixels.PixelViewModel

class PixelActivity: AppCompatActivity() {

    private lateinit var viewBinding: ActivityPixelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model: PixelViewModel by viewModels()
        viewBinding = ActivityPixelBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val uri = intent.getParcelableExtra<Uri>("imageUrl")
//        val canvas = findViewById<PixelCanvas>(R.id.pixel_canvas)

//        findViewById<ImageView>(R.id.target_image_view)
//        viewBinding.targetImageView.setImageURI(uri)

        viewBinding.beforeButton.setOnClickListener {
            runOnUiThread {
                if (model.showBefore()) {
                    updatePixels(model)
                }
            }
        }

        viewBinding.nextButton.setOnClickListener {
            runOnUiThread {
                if (model.showNext()) {
                    updatePixels(model)
                }
            }
        }

        runOnUiThread {
            model.getFirstColorList(applicationContext, uri)
            updatePixels(model)
        }
    }

    private fun updatePixels(model: PixelViewModel) {
        viewBinding.pixelCanvas.set(model.colors, model.pixelWidth, model.pixelHeight)
    }
}