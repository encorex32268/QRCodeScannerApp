package com.lihan.qrcodescannerapp

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun QRCodeScreen(
    modifier: Modifier
) {
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(localContext)
    }
    var scannedText by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                preview.setSurfaceProvider(previewView.surfaceProvider)

                runCatching {
                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        ImageAnalysis.Builder().build().also { imageAnalysis ->
                            imageAnalysis.setAnalyzer(
                                ContextCompat.getMainExecutor(context),
                                BarcodeAnalyzer(
                                    onSuccess = {
                                        scannedText = it.first().rawValue?:""
                                        Log.e("CAMERA", "Camera bind success ${it.first().rawValue}")
                                    },
                                    onFailed = {
                                        Log.e("CAMERA", "Camera bind error ${it.localizedMessage}", it)
                                    },
                                    onIdle = {
                                        scannedText = ""
                                    }
                                )
                            )
                        }
                    )
                }.onFailure {
                    Log.e("CAMERA", "Camera bind error ${it.localizedMessage}", it)
                }
                previewView

            }
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter).padding(
                    top = 36.dp
                ),
            text = scannedText,
            fontSize = 36.sp,
            color = Color.White
        )
    }

}