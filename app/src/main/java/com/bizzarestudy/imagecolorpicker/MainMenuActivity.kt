package com.bizzarestudy.imagecolorpicker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bizzarestudy.imagecolorpicker.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraXApp"
    }
    // 뷰바인딩
    private lateinit var viewBinding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        viewBinding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.takePicture.setOnClickListener {
            startActivity(Intent(applicationContext, TakingPictureActivity::class.java))
        }

        val launcher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result?.resultCode == RESULT_OK) {
                val intent = result.data
                val uri = intent?.data
                Log.d(TAG, "load image uri : " + uri.toString())

                if (uri != null) {
                    Log.d(TAG, "load image uri is not null")
                    val intentToPixel = Intent(applicationContext, PixelizedImageActivity::class.java)
                    intentToPixel.putExtra("imageUrl", uri)
                    startActivity(intentToPixel)
                }
            }
        }

        viewBinding.loadPicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            launcher.launch(intent)
        }



    }
}