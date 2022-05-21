package com.bizzarestudy.imagecolorpicker

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.bizzarestudy.imagecolorpicker.databinding.ActivityPixelizedImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation


class PixelizedImageActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraXApp"
        private const val INTERVAL = 40
        private const val SIDE_LENGTH = INTERVAL * 2 - 1
    }

    // 뷰바인딩
    private lateinit var viewBinding: ActivityPixelizedImageBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPixelizedImageBinding.inflate(layoutInflater)
        viewBinding.shareButton.isEnabled = false
        setContentView(viewBinding.root)

        val intent = intent

        if (intent.hasExtra("imageUrl")) {
            Log.d(TAG, "imageUrl : " + intent.getParcelableExtra<Uri>("imageUrl").toString())
        } else {
            Log.d(TAG, "intent extra null")
        }

        //이미지 로딩
        Glide.with(applicationContext).asBitmap()
            .load(intent.getParcelableExtra<Uri>("imageUrl"))
            .transform(PixelationFilterTransformation(50f))
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    viewBinding.imageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        viewBinding.shareButton.setOnClickListener {
            val textIntent = Intent(Intent.ACTION_SEND)
            textIntent.type = "text/plain"
            textIntent.putExtra(Intent.EXTRA_TEXT, viewBinding.hexColorCode.text)
            startActivity(Intent.createChooser(textIntent, "색상 hex code 공유"))
        }

        //이미지 로딩 후 리스너 별도로 등록
        viewBinding.imageView.setOnTouchListener(object : View.OnTouchListener {

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                val bitmap = view!!.drawToBitmap(Bitmap.Config.ARGB_8888)
                Log.d(TAG,"Touch coordinates : motionEvent.x, ${motionEvent.y}")
                //0,0,0및 알파값도 표시
                if (motionEvent.x < 0 || motionEvent.y < 0){
                    viewBinding.alphaValue.text = "x,y= -"
                    viewBinding.redValue.text = ""
                    viewBinding.blueValue.text = ""
                    viewBinding.greenValue.text = ""
                    return false
                }
                if (motionEvent.x > bitmap.width || motionEvent.y > bitmap.height){
                    viewBinding.alphaValue.text = "x,y= +"
                    viewBinding.redValue.text = ""
                    viewBinding.blueValue.text = ""
                    viewBinding.greenValue.text = ""
                    return false
                }

                when (motionEvent.action) {
                    MotionEvent.ACTION_MOVE,
                    MotionEvent.ACTION_DOWN -> {

                        val pixelX = motionEvent.getAxisValue(MotionEvent.AXIS_X).toInt()
                        val pixelY = motionEvent.getAxisValue(MotionEvent.AXIS_Y).toInt()
                        val pixelColorInt = bitmap.getPixel(pixelX, pixelY)
                        val pixelColorHexString = "#${Integer.toHexString(pixelColorInt)}"

                        viewBinding.alphaValue.text = Color.alpha(pixelColorInt).toString()
                        viewBinding.redValue.text = Color.red(pixelColorInt).toString()
                        viewBinding.blueValue.text = Color.blue(pixelColorInt).toString()
                        viewBinding.greenValue.text = Color.green(pixelColorInt).toString()
                        viewBinding.hexColorCode.text = pixelColorHexString

                        if(pixelX > 1 && pixelY > 1 && pixelX < bitmap.width && pixelY < bitmap.height) {
                            viewBinding.subregion.setImageBitmap(Bitmap.createBitmap(
                                bitmap,
                                when {
                                    pixelX - INTERVAL < 1 -> 1
                                    pixelX + INTERVAL > bitmap.width -> bitmap.width - SIDE_LENGTH
                                    else -> pixelX - INTERVAL
                                },
                                when {
                                    pixelY - INTERVAL < 1 -> 1
                                    pixelY + INTERVAL > bitmap.height -> bitmap.height - SIDE_LENGTH
                                    else -> pixelY - INTERVAL
                                },
                                SIDE_LENGTH,
                                SIDE_LENGTH
                            ))
                            viewBinding.pixelColor.setImageBitmap(
                                Bitmap.createBitmap(
                                    bitmap,
                                    pixelX,
                                    pixelY,
                                    1,
                                    1
                                )
                            )
                        }
                        viewBinding.shareButton.isEnabled = true
                        return true
                    }
                }
                return false
            }
        })
    }
}
