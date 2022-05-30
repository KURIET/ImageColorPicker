package com.bizzarestudy.imagecolorpicker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bizzarestudy.imagecolorpicker.databinding.ActivityImageProcessBinding
import com.bizzarestudy.imagecolorpicker.presentation.pixels.PixelActivity

class ImageProcessActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraXApp"
    }
    // 뷰바인딩
    private lateinit var viewBinding: ActivityImageProcessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        viewBinding = ActivityImageProcessBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.useFilter.setOnClickListener {

        }

        viewBinding.convertPixel.setOnClickListener {
            val uri = intent.getParcelableExtra<Uri>("imageUrl")
            startActivity(Intent(applicationContext, PixelActivity::class.java).apply {
                putExtra("imageUrl", uri)
            })
        }



    }
}