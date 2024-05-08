package id.co.minumin.presentation.view

import android.graphics.Color
import android.graphics.Color.BLUE
import id.co.minumin.const.MinuminConstant.ANGLE.ANGLE_90
import id.co.minumin.presentation.view.CircularProgressBar.Companion.LINEAR

/**
 * Created by pertadima on 11,February,2021
 */

internal class CircularProgressOptions {

    var progress = 0
        set(value) {
            if (value < 0)
                throw IllegalArgumentException("Progress can't be negative")
            else {
                field = value
            }
        }


    var maxProgress = 0
        set(value) {
            if (value < 0) throw IllegalArgumentException("Maximum Progress can't be negative")
            else {
                field = value
            }
        }

    var progressColor = BLUE
    var progressWidth = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException("Progress Width can't be negative")
            else {
                field = value
            }
        }

    var progressBackgroundWidth = 0f
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("Progress Background Width can't be negative")
            } else {
                field = value
            }
        }

    var progressBackgroundColor = Color.LTGRAY
    var disableDefaultSweep = false

    //TODO - add text
    var textColor = Color.BLACK

    var textSize = 0f

    var startAngle = 0
        set(value) {
            field = value - ANGLE_90
        }
        get() = field + ANGLE_90


    var enableBackgroundDashEffect = false

    var showDot = false

    var progressCap = CircularProgressBar.ROUND
        set(value) {
            if (value == 0 || value == 1) field = value
            else throw IllegalArgumentException("Invalid Progress Cap")
        }

    var dotWidth = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException("Dot Width can't be negative")
            else field = value
        }

    var dotColor = BLUE

    var dashLineLength = DASH_LINE_LENGTH
        set(value) {
            if (value < 0) throw IllegalArgumentException("Dash Line Length can't be negative")
            else field = value
        }

    var dashLineOffset = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException("Dash Line Offset can't be negative")
            else field = value
        }

    var interpolator = LINEAR
        set(value) {
            if (value == 0 || value == 1 || value == 2) field = value
            else throw IllegalArgumentException("Invalid Interpolator")
        }

    var animationDuration = 0
        set(value) {
            if (value < 0) throw IllegalArgumentException("Animation Duration can't be negative")
            else field = value
        }

    var fadeRepeatCount = 0

    var minFadeAlpha = MIN_FADE_ALPHA
        set(value) {
            if (value < 0 || value > MAX_ALPHA) {
                throw IllegalArgumentException(
                    "Alpha value should be in range of 0 to 255 inclusive"
                )
            } else field = value
        }

    companion object {
        private const val MIN_FADE_ALPHA = 85
        private const val MAX_ALPHA = 255
        private const val DASH_LINE_LENGTH = 8
    }
}