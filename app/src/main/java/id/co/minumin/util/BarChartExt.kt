package id.co.minumin.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.model.GradientColor
import id.co.minumin.R
import id.co.minumin.core.ext.getColorCompat

/**
 * Created by pertadima on 09,February,2021
 */

fun BarChart?.addAdditionalEffect(
    markerView: MarkerView,
    minChartValue: Float = 0F,
    maxChartValue: Float = 0F
) {
    this?.run {
        setScaleEnabled(false)
        markerView.chartView = this
        marker = markerView
        description.isEnabled = false
        legend.isEnabled = false
        isDoubleTapToZoomEnabled = false
        setPinchZoom(false)
        setDrawBarShadow(false)
        setDrawValueAboveBar(false)

        axisLeft.addAdditionalEffectForLeft(context, minChartValue, maxChartValue)
        axisRight.addAdditionalEffectForRight(context, minChartValue, maxChartValue)
        xAxis.addAdditionalEffectForBottom()

        renderer = CustomBarChartRender(this, animator, viewPortHandler)
    }
}

private fun YAxis.addAdditionalEffectForLeft(
    context: Context,
    minChartValue: Float,
    maxChartValue: Float
) {
    isEnabled = false
    gridColor = context.getColorCompat(R.color.line_gray)
    axisMinimum = minChartValue
    if (maxChartValue > 0F) {
        axisMaximum = maxChartValue
    }
}

private fun YAxis.addAdditionalEffectForRight(
    context: Context,
    minChartValue: Float,
    maxChartValue: Float
) {
    textColor = Color.WHITE
    setDrawAxisLine(false)
    gridColor = context.getColorCompat(R.color.line_gray)
    axisMinimum = minChartValue
    if (maxChartValue > 0F) {
        axisMaximum = maxChartValue
    }
}

private fun XAxis.addAdditionalEffectForBottom() {
    position = XAxis.XAxisPosition.BOTTOM
    setDrawGridLines(false)
    textColor = Color.WHITE
}

fun Context.getBarDataSetAdditionalEffect(
    barEntries: List<BarEntry>,
    @ColorRes startColorResId: Int,
    @ColorRes endColorResId: Int
): BarDataSet =
    BarDataSet(barEntries, "").apply {
        setDrawValues(false)
        gradientColors = mutableListOf(
            GradientColor(
                getColorCompat(startColorResId),
                getColorCompat(endColorResId)
            )
        )
        highLightAlpha = 0
    }