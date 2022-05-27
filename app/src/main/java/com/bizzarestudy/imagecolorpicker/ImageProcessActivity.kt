package com.bizzarestudy.imagecolorpicker

import android.content.Intent
import android.net.Uri
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
            val nextIntent = Intent(applicationContext, PixelActivity::class.java)
            val uri = intent.getParcelableExtra<Uri>("imageUrl")
            Log.i("KM-01", uri.toString())
            nextIntent.putExtra("imageUrl", uri)
            startActivity(nextIntent)
        }



    }
}