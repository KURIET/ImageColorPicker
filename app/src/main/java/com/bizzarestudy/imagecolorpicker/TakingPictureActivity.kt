package com.bizzarestudy.imagecolorpicker

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bizzarestudy.imagecolorpicker.databinding.TakingPictureBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakingPictureActivity : AppCompatActivity() {

    // https://developer.android.com/codelabs/camerax-getting-started

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    // 뷰바인딩
    private lateinit var viewBinding: TakingPictureBinding
    // 이미지 캡쳐
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = TakingPictureBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 카메라 권한 요청 (되어있다면 카메라 활성화)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    // 권한
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // 카메라 Preview 시작 function
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({

            // 아래 코드는 카메라의 생명주기를 얻어 Lifecycle Owner에 바인딩하는 코드이다.
            // CameraX 사용 간에 카메라를 활성화/비활성화하는 수고를 덜어준다.
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // xml의 PreviewView 내 surface provider를 찾아 Preview를 설정한다.
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            //
            imageCapture = ImageCapture.Builder().build()

            // 후면 카메라를 기본으로 선택한다.
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 바인딩하기 이전으로 cameraProvider에 바인딩 된 것이 없는지 확인한다.
                cameraProvider.unbindAll()

                // cameraProvider에 cameraSelector와 preview 객체를 바인딩한다.
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    // ImageCapture의 사용예
    // "Take Photo" 버튼이 눌리면 실행되는 function
    private fun takePhoto() {
        // 수정 가능한 ImageCapture 객체를 받았는지 확인한다. 아니라면 app crash가 발생한다.
        val imageCapture = imageCapture ?: return

        // MediaStore의 contentValues를 설정해준다.
        // 저장경로, 날짜포맷을 딴 이미지명, 확장자 지정을 한다.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }


        // OutputFileOptions 객체를 만들어 결과물의 옵션을(메타데이터) 설정한다.
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // 사진이 찍히고 난 뒤에 호출되는 image capture listener를 설정한다.
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    val intent = Intent(applicationContext, ImageProcessActivity::class.java)
                    intent.putExtra("imageUrl", output.savedUri)
                    startActivity(intent)
                }
            }
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}