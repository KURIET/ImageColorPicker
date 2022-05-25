package com.bizzarestudy.imagecolorpicker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bizzarestudy.imagecolorpicker.databinding.ActivityImageProcessBinding

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
            startActivity(Intent(applicationContext, PixelActivity::class.java))
        }



    }
}