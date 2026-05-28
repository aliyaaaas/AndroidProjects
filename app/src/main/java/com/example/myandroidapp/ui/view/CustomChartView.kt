package com.example.myandroidapp.ui.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myandroidapp.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CustomChartView(
    sectors: List<Pair<Int, Int>>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    gapAngle: Float = 4f,
    thickness: Dp = 40.dp
) {
    val context = LocalContext.current

    val totalPercentage = sectors.sumOf { it.second }
    if (totalPercentage != 100) {
        throw IllegalArgumentException(
            context.getString(R.string.error_total_percentage, totalPercentage)
        )
    }

    if (colors.size < sectors.size) {
        throw IllegalArgumentException(
            context.getString(R.string.error_colors_count)
        )
    }

    if (sectors.size > 1) {
        for (i in 0 until sectors.size) {
            val nextIndex = (i + 1) % sectors.size
            if (colors[i] == colors[nextIndex]) {
                throw IllegalArgumentException(
                    context.getString(R.string.error_chart_colors)
                )
            }
        }
    }

    var selectedKey by remember { mutableStateOf<Int?>(null) }

    val density = LocalDensity.current
    val thicknessPx = remember(thickness) { with(density) { thickness.toPx() } }
    val selectedBonusWidthPx = remember { with(density) { 6.dp.toPx() } }
    val percentageFormat = stringResource(R.string.chart_percentage_format)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(sectors) {
                detectTapGestures { tapOffset ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f

                    val x = tapOffset.x - centerX
                    val y = tapOffset.y - centerY

                    val touchRadius = sqrt(x * x + y * y)
                    val outerRadius = Math.min(centerX, centerY)
                    val innerRadius = outerRadius - thicknessPx

                    if (touchRadius in innerRadius..outerRadius) {
                        var angleDeg = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
                        if (angleDeg < 0) angleDeg += 360f

                        val adjustedTapAngle = (angleDeg + 90f) % 360f

                        var currentStartAngle = 0f
                        var foundKey: Int? = null

                        for (sector in sectors) {
                            val sweepAngle = (sector.second / 100f) * 360f
                            val endAngle = currentStartAngle + sweepAngle

                            if (adjustedTapAngle >= currentStartAngle && adjustedTapAngle < endAngle) {
                                foundKey = sector.first
                                break
                            }
                            currentStartAngle = endAngle
                        }
                        selectedKey = foundKey
                    } else {
                        selectedKey = null
                    }
                }
            }
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = Math.min(centerX, centerY) - (thicknessPx / 2f)

        val arcSize = Size(radius * 2, radius * 2)
        val topLeft = Offset(centerX - radius, centerY - radius)

        var startAngle = -90f

        sectors.forEachIndexed { index, sector ->
            val percentage = sector.second
            val key = sector.first

            val totalSweepAngle = (percentage / 100f) * 360f
            val drawSweepAngle = totalSweepAngle - gapAngle
            val drawStartAngle = startAngle + (gapAngle / 2f)

            val isSelected = key == selectedKey

            val baseColor = colors[index]
            val sectorColor = if (isSelected) baseColor.copy(alpha = 1.0f) else baseColor.copy(alpha = 0.65f)
            val strokeWidth = if (isSelected) thicknessPx + selectedBonusWidthPx else thicknessPx

            drawArc(
                color = sectorColor,
                startAngle = drawStartAngle,
                sweepAngle = drawSweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            val middleAngleRad = Math.toRadians((startAngle + totalSweepAngle / 2f).toDouble())
            val textX = centerX + radius * cos(middleAngleRad).toFloat()
            val textY = centerY + radius * sin(middleAngleRad).toFloat()

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 40f
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    alpha = if (isSelected) 255 else (0.65f * 255).toInt()
                }

                val fontMetrics = paint.fontMetrics
                val yOffset = (fontMetrics.descent + fontMetrics.ascent) / 2f
                val textToDraw = String.format(percentageFormat, percentage)

                drawText(
                    textToDraw,
                    textX,
                    textY - yOffset,
                    paint
                )
            }

            startAngle += totalSweepAngle
        }
    }
}