package com.lihan.qrcodescannerapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScannerOverlayBox(
    boxWidth: Dp = 250.dp,
    boxHeight: Dp = 250.dp,
    borderColor: Color = Color.Green,
    borderWidth: Dp = 2.dp
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val boxLeft = (canvasWidth - boxWidth.toPx()) / 2
            val boxTop = (canvasHeight - boxHeight.toPx()) / 2
            val boxRight = boxLeft + boxWidth.toPx()
            val boxBottom = boxTop + boxHeight.toPx()

            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size
            )

            drawRect(
                color = Color.Transparent,
                topLeft = Offset(boxLeft, boxTop),
                size = Size(
                    width = boxRight - boxLeft,
                    height = boxBottom - boxTop
                ),
                blendMode = BlendMode.Clear
            )

            drawRect(
                color = borderColor,
                topLeft = Offset(boxLeft, boxTop),
                size = Size(
                    width = boxRight - boxLeft,
                    height = boxBottom - boxTop
                ),
                style = Stroke(width = borderWidth.toPx())
            )
        }
    }
}