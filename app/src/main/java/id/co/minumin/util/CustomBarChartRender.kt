package id.co.minumin.util

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil

/**
 * Created by pertadima on 09,February,2021
 */

class CustomBarChartRender(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val barShadowRectBuffer = RectF()

    /**
     * HUNGARIAN NOTATION COME FROM LIBRARY (BAR CHART RENDERER CLASS)
     */


    override fun drawDataSet(canvas: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.apply {
            color = dataSet.barBorderColor
            strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        }
        mShadowPaint.color = dataSet.barShadowColor


        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor
            val barData = mChart.barData
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / HALF_SIZE
            var x: Float
            var i = 0
            val count =
                ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).coerceAtMost(dataSet.entryCount.toDouble())

            while (i < count) {
                val barEntry = dataSet.getEntryForIndex(i)
                x = barEntry.x

                barShadowRectBuffer.apply {
                    left = x - barWidthHalf
                    right = x + barWidthHalf
                }
                trans.rectValueToPixel(barShadowRectBuffer)

                if (mViewPortHandler.isInBoundsLeft(barShadowRectBuffer.right).not()) {
                    i++
                    continue
                }

                if (mViewPortHandler.isInBoundsRight(barShadowRectBuffer.left).not()) break
                barShadowRectBuffer.apply {
                    top = mViewPortHandler.contentTop()
                    bottom = mViewPortHandler.contentBottom()
                }
                i++
            }
        }
        // initialize the buffer
        val barBuffer = mBarBuffers[index].apply {
            setPhases(phaseX, phaseY)
            setDataSet(index)
            setInverted(mChart.isInverted(dataSet.axisDependency))
            setBarWidth(mChart.barData.barWidth)
            feed(dataSet)
        }

        trans.pointValuesToPixel(barBuffer.buffer)
        val isSingleColor = dataSet.colors.size == BUFFER_TOP_INDEX
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }
        var j = 0
        while (j < barBuffer.size()) {
            if (mViewPortHandler.isInBoundsLeft(barBuffer.buffer[j + BUFFER_RIGHT_INDEX]).not()) {
                j += BUFFER_MAX_INDEX
                continue
            }
            if (mViewPortHandler.isInBoundsRight(barBuffer.buffer[j]).not()) break

            // Set the color for the currently drawn value. If the index | is out of bounds, reuse colors.
            if (isSingleColor.not()) {
                mRenderPaint.color = dataSet.getColor(j / BUFFER_MAX_INDEX)
            }

            if (dataSet.gradientColor != null) {
                val gradientColor = dataSet.gradientColor
                with(barBuffer) {
                    mRenderPaint.shader = android.graphics.LinearGradient(
                        buffer[j],
                        buffer[j + BUFFER_BOTTOM_INDEX],
                        buffer[j],
                        buffer[j + BUFFER_TOP_INDEX],
                        gradientColor.startColor,
                        gradientColor.endColor,
                        android.graphics.Shader.TileMode.MIRROR
                    )
                }
            }
            if (dataSet.gradientColors != null) {
                with(barBuffer) {
                    mRenderPaint.shader = android.graphics.LinearGradient(
                        buffer[j],
                        buffer[j + BUFFER_BOTTOM_INDEX],
                        buffer[j],
                        buffer[j + BUFFER_TOP_INDEX],
                        dataSet.getGradientColor(j / BUFFER_MAX_INDEX).startColor,
                        dataSet.getGradientColor(j / BUFFER_MAX_INDEX).endColor,
                        android.graphics.Shader.TileMode.MIRROR
                    )
                }
            }

            with(barBuffer) {
                val path2: Path =
                    roundRect(
                        RectF(
                            buffer[j],
                            buffer[j + BUFFER_TOP_INDEX],
                            buffer[j + BUFFER_RIGHT_INDEX],
                            buffer[j + BUFFER_BOTTOM_INDEX]
                        )
                    )
                canvas.drawPath(path2, mRenderPaint)
            }

            if (drawBorder) {
                with(barBuffer) {
                    val path: Path = roundRect(
                        RectF(
                            buffer[j],
                            buffer[j + BUFFER_TOP_INDEX],
                            buffer[j + BUFFER_RIGHT_INDEX],
                            buffer[j + BUFFER_BOTTOM_INDEX]
                        )
                    )
                    canvas.drawPath(path, mBarBorderPaint)
                }
            }
            j += BUFFER_MAX_INDEX
        }
    }

    private fun roundRect(
        rect: RectF,
        isRoundedTopLeft: Boolean = true,
        isRoundedTopRight: Boolean = true,
        isRoundedBottomRight: Boolean = false,
        isRoundedBottomLeft: Boolean = false
    ): Path {
        var rx = rect.width() / HALF_SIZE
        var ry = rect.width() / HALF_SIZE
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom

        if (rx < DEFAULT_VALUE) rx = DEFAULT_VALUE
        if (ry < DEFAULT_VALUE) ry = DEFAULT_VALUE

        val width = right - left
        val height = bottom - top

        if (rx > width / HALF_SIZE) rx = width / HALF_SIZE
        if (ry > height / HALF_SIZE) ry = height / HALF_SIZE

        val widthMinusCorners = width - HALF_SIZE * rx
        val heightMinusCorners = height - HALF_SIZE * ry

        return Path().apply {
            moveTo(right, top + ry)

            if (isRoundedTopRight) rQuadTo(DEFAULT_VALUE, -ry, -rx, -ry) //top-right corner
            else {
                rLineTo(DEFAULT_VALUE, -ry)
                rLineTo(-rx, DEFAULT_VALUE)
            }
            rLineTo(-widthMinusCorners, DEFAULT_VALUE)

            if (isRoundedTopLeft) rQuadTo(-rx, DEFAULT_VALUE, -rx, ry) //top-left corner
            else {
                rLineTo(-rx, DEFAULT_VALUE)
                rLineTo(DEFAULT_VALUE, ry)
            }
            rLineTo(DEFAULT_VALUE, heightMinusCorners)

            if (isRoundedBottomLeft) rQuadTo(DEFAULT_VALUE, ry, rx, ry) //bottom-left corner
            else {
                rLineTo(DEFAULT_VALUE, ry)
                rLineTo(rx, DEFAULT_VALUE)
            }
            rLineTo(widthMinusCorners, DEFAULT_VALUE)

            if (isRoundedBottomRight) rQuadTo(rx, DEFAULT_VALUE, rx, -ry) //bottom-right corner
            else {
                rLineTo(rx, DEFAULT_VALUE)
                rLineTo(DEFAULT_VALUE, -ry)
            }
            rLineTo(DEFAULT_VALUE, -heightMinusCorners)

            close() //Given close, last lineto can be removed.
        }
    }

    companion object {
        private const val DEFAULT_VALUE = 0F
        private const val HALF_SIZE = 2F
        private const val BUFFER_MAX_INDEX = 4
        private const val BUFFER_TOP_INDEX = 1
        private const val BUFFER_RIGHT_INDEX = 2
        private const val BUFFER_BOTTOM_INDEX = 3
    }
}