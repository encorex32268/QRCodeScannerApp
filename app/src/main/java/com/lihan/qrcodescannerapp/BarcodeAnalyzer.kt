package com.lihan.qrcodescannerapp

import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class BarcodeAnalyzer(
    private val onSuccess: (List<Barcode>) -> Unit,
    private val onFailed: (Exception) -> Unit,
    private val onIdle: () -> Unit = {},
    private val scanDelayMillis: Long = 2000L,
    private val idleTimeoutMillis: Long = 3000L
): ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)
    private var isScanningAllowed = true
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var idleJob: Job? = null

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (!isScanningAllowed){
            imageProxy.close()
            return
        }

        imageProxy.image?.let { image ->
            scanner.process(
                InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                )
            ).addOnSuccessListener { barcode ->

                barcode?.let {
                    if(it.isNotEmpty()){
                        isScanningAllowed = false
                        val imageWidth = imageProxy.width
                        val imageHeight = imageProxy.height

                        val left = imageWidth * 0.25
                        val top = imageHeight * 0.25
                        val right = imageWidth * 0.75
                        val bottom = imageHeight * 0.75

                        val targetRect = Rect(
                            left.roundToInt(),
                            top.roundToInt(),
                            right.roundToInt(),
                            bottom.roundToInt()
                        )
                        val filtered = it.filter { it.boundingBox?.let { box ->
                            targetRect.contains(box.centerX(), box.centerY())
                        } == true }

                        if (filtered.isNotEmpty()){
                            onSuccess(it)

                            idleJob?.cancel()
                            idleJob = coroutineScope.launch {
                                delay(idleTimeoutMillis)
                                onIdle()
                            }
                        }

                        coroutineScope.launch {
                            delay(scanDelayMillis)
                            isScanningAllowed = true
                        }
                    }
                }
                imageProxy.close()
            }.addOnFailureListener {
                onFailed(it)
                imageProxy.close()
            }
        }


    }
}