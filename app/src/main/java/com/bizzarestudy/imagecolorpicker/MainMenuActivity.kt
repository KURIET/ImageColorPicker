package com.bizzarestudy.imagecolorpicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

        val launcher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult>{
                override fun onActivityResult(result: ActivityResult?) {
                    if (result?.resultCode == RESULT_OK){
                        val intent = result.data
                        val uri = intent?.data
                        Log.d(TAG,"load image uri : " + uri.toString())

                        if(uri != null){
                            Log.d(TAG,"load image uri is not null")
                            var intent2 = Intent(applicationContext, PixelizedImageActivity::class.java)
                            intent2.putExtra("imageUrl", uri)
                            startActivity(intent2)
                        }
                    }
                }
            })

        viewBinding.loadPicture.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            launcher.launch(intent)
        }



    }
}