package com.bizzarestudy.imagecolorpicker.presentation.pixels

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bizzarestudy.imagecolorpicker.databinding.ActivityPixelBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PixelActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var viewBinding: ActivityPixelBinding
    private val model: PixelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPixelBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val uri = intent.getParcelableExtra<Uri>("imageUrl")

        launch {
            runOnUiThread {
                model.getFirstColorList(applicationContext, uri)
                updatePixels(model)
            }
        }

        viewBinding.beforeButton.setOnClickListener {
            launch {
                runOnUiThread {
                    if (model.showBefore()) {
                        updatePixels(model)
                    }
                }
            }
        }

        viewBinding.nextButton.setOnClickListener {
            launch {
                runOnUiThread {
                    if (model.showNext()) {
                        updatePixels(model)
                    }
                }
            }
        }
    }

    private fun updatePixels(model: PixelViewModel) {
        viewBinding.pixelCanvas.set(model.colors, model.pixelWidth, model.pixelHeight)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job
}