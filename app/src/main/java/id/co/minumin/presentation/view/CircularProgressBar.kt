package id.co.minumin.presentation.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import id.co.minumin.R
import id.co.minumin.const.MinuminConstant.ANGLE.ANGLE_360
import id.co.minumin.const.MinuminConstant.ANGLE.ANGLE_90
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Created by pertadima on 11,February,2021
 */

class CircularProgressBar : View {

    private var listener: (ValueAnimator) -> Unit = { _ -> kotlin.run { } }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        currentAnimatedProgress = 0f
        refresh()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    private val mProgressAnimator = ValueAnimator()
    private val mAlphaAnimator = ValueAnimator()
    private var currentAnimatedProgress = 0f
    private var options = CircularProgressOptions()


    /**
     * Progress.
     */
    var progress
        set(value) {
            options.progress = if (value < 0) 0 else value
            refresh()
        }
        get() = options.progress


    /**
     * Max Progress.
     * Default value is 100.
     * Should be a positive number.
     */
    private var maxProgress
        set(value) {
            if (value < 0) throw IllegalArgumentException("Maximum Progress can't be negative")
            else {
                options.maxProgress = value
                currentAnimatedProgress = 0f
                refresh()
            }
        }
        get() = options.maxProgress


    /**
     * Progress stroke color.
     */
    private var progressColor
        set(value) {
            options.progressColor = value
            refresh()
        }
        get() = options.progressColor


    /**
     * Progress stroke width.
     */
    var progressWidth
        set(value) {
            if (value < 0) throw IllegalArgumentException("Progress Width can't be negative")
            else {
                options.progressWidth = value
                refresh()
            }
        }
        get() = options.progressWidth


    /**
     * Progress background stroke width.
     */
    var progressBackgroundWidth
        set(value) {
            if (value < 0) throw IllegalArgumentException("Progress Background Width can't be negative")
            else {
                options.progressBackgroundWidth = value
                refresh()
            }
        }
        get() = options.progressBackgroundWidth


    /**
     * Progress background stroke color.
     */
    private var progressBackgroundColor
        set(value) {
            options.progressBackgroundColor = value
            refresh()
        }
        get() = options.progressBackgroundColor


    //TODO - add text
    /*private var textColor
        set(value) {
            options.textColor = value
            refresh()
        }
        get() = options.textColor*/


    /*private var textSize
        set(value) {
            options.textSize = value
            refresh()
        }
        get() = options.textSize*/


    /**
     * Start angle of progress bar.
     * Default value is 0 degree.
     */
    var startAngle
        set(value) {
            options.startAngle = value
            refresh()
        }
        get() = options.startAngle


    /**
     * Enable background dash effect.
     * Default value is false.
     */
    var enableBackgroundDashEffect
        set(value) {
            options.enableBackgroundDashEffect = value
            refresh()
        }
        get() = options.enableBackgroundDashEffect


    /**
     * Show dot flag.
     * Default value is true.
     */
    var showDot
        set(value) {
            options.showDot = value
            refresh()
        }
        get() = options.showDot


    /**
     * Progress cap has two options - round and flat.
     * Default is round.
     */
    var progressCap
        set(value) {
            if (value == 0 || value == 1) {
                options.progressCap = value
                refresh()
            } else throw IllegalArgumentException("Invalid Progress Cap")
        }
        get() = options.progressCap


    /**
     * Dot Width.
     */
    private var dotWidth
        set(value) {
            if (value < 0) throw IllegalArgumentException("Dot Width can't be negative")
            else {
                options.dotWidth = value
                refresh()
            }
        }
        get() = options.dotWidth


    /**
     * Dot Color.
     */
    private var dotColor
        set(value) {
            options.dotColor = value
            refresh()
        }
        get() = options.dotColor


    /**
     * Length of dash line.
     */
    var dashLineLength
        set(value) {
            if (value < 0) throw IllegalArgumentException("Dash Line Length can't be negative")
            else {
                options.dashLineLength = value
                refresh()
            }
        }
        get() = options.dashLineLength


    /**
     * Lenght of dash Offset.
     */
    var dashLineOffset
        set(value) {
            if (value < 0) throw IllegalArgumentException("Dash Line Offset can't be negative")
            else {
                options.dashLineOffset = value
                refresh()
            }
        }
        get() = options.dashLineOffset


    /**
     * Interpolator which provides accelerate, decelerate and linear interpolators.
     */
    var interpolator
        set(value) {
            if (value == 0 || value == 1 || value == 2) {
                options.interpolator = value
            } else throw IllegalArgumentException("Invalid Interpolator")
        }
        get() = options.interpolator


    /**
     * In case of fade animation, animationDuration denotes single fade in fade out duration.
     * In case of sweep animation, animationDuration denotes sweep time.
     */
    var animationDuration
        set(value) {
            if (value < 0) throw IllegalArgumentException("Animation Duration can't be negative")
            else options.animationDuration = value
        }
        get() = options.animationDuration


    /**
     * fade animation repeat count (not total fade in fade out animation).
     */
    var fadeRepeatCount
        set(value) {
            options.fadeRepeatCount = value
        }
        get() = options.fadeRepeatCount


    /**
     * disable default sweep whenever progress changes.
     * Default value - false
     */
    var disableDefaultSweep
        set(value) {
            options.disableDefaultSweep = value
        }
        get() = options.disableDefaultSweep


    /**
     * For fade in fade out animation. Animate view from minFadeAlpha to DEFAULT_ALPHA (opaque).
     */
    private var minFadeAlpha
        set(value) {
            if (value < 0 || value > DEFAULT_ALPHA) {
                throw IllegalArgumentException("Alpha value should be in range of 0 to DEFAULT_ALPHA inclusive")
            } else options.minFadeAlpha = value
        }
        get() = options.minFadeAlpha


    private var circularProgressBarAlpha = DEFAULT_ALPHA
        set(value) {
            field = when {
                value < 0 -> 0
                value > DEFAULT_ALPHA -> DEFAULT_ALPHA
                else -> value
            }
        }

    private var mSize = 0
    private var mRadius = 0f
    private var mDefaultSize: Int = 0

    private val mForegroundStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBackgroundStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
        setAnimators()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawOutlineArc(canvas)
        if (showDot) drawDot(canvas)
    }

    private fun drawOutlineArc(canvas: Canvas) {
        mBackgroundStrokePaint.alpha = circularProgressBarAlpha
        mForegroundStrokePaint.alpha = circularProgressBarAlpha
        dotPaint.alpha = circularProgressBarAlpha
        canvas.drawCircle(mSize / 2.0f, mSize / 2.0f, mRadius, mBackgroundStrokePaint)
        val pad = mSize / 2 - mRadius
        val outerOval = RectF(pad, pad, mSize.toFloat() - pad, mSize.toFloat() - pad)
        mForegroundStrokePaint.strokeCap =
            if (progressCap == ROUND) Paint.Cap.ROUND else Paint.Cap.BUTT

        val angle = (currentAnimatedProgress * ANGLE_360) / maxProgress
        canvas.drawArc(
            outerOval,
            startAngle.toFloat() - ANGLE_90,
            angle,
            false,
            mForegroundStrokePaint
        )
    }

    private fun drawDot(canvas: Canvas) {
        val angle: Double =
            (currentAnimatedProgress * ANGLE_360) / maxProgress.toDouble() + startAngle
        val x = mSize / 2 + mRadius * sin(Math.toRadians(angle))
        val y = mSize / 2 - mRadius * cos(Math.toRadians(angle))
        canvas.drawCircle(x.toFloat(), y.toFloat(), dotWidth / 2, dotPaint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val defaultSize = mDefaultSize
        val defaultWidth = max(suggestedMinimumWidth, defaultSize)

        val width = when (widthMode) {
            EXACTLY -> widthSize
            AT_MOST -> min(defaultWidth, widthSize)
            UNSPECIFIED -> defaultWidth
            else -> defaultWidth
        }
        val height = when (heightMode) {
            EXACTLY -> heightSize
            AT_MOST -> width//Math.min(defaultHeight, heightSize)
            UNSPECIFIED -> width//defaultHeight
            else -> width//defaultHeight
        }
        mSize = min(width, height)
        mRadius = (mSize - max(max(progressBackgroundWidth, progressWidth), dotWidth)) / 2.0f
        setMeasuredDimension(mSize, mSize)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val displayMetrics = context.resources.displayMetrics
        mDefaultSize = (DEFAULT_SIZE_DP * displayMetrics.density).roundToInt()

        mForegroundStrokePaint.isAntiAlias = true
        mBackgroundStrokePaint.isAntiAlias = true
        dotPaint.isAntiAlias = true
        mForegroundStrokePaint.style = STROKE
        mBackgroundStrokePaint.style = STROKE
        dotPaint.style = Paint.Style.FILL

        if (attrs == null) setupOptionsAttrNull(displayMetrics)
        else {
            context.theme.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0)
                .apply {
                    setupOptionsWithStyling(displayMetrics)
                    recycle()
                }
        }


        circularProgressBarAlpha = DEFAULT_ALPHA
        options.dashLineLength = DEFAULT_DASH_LINE_LENGTH * displayMetrics.density
        options.dashLineOffset = DEFAULT_DASH_LINE_OFFSET * displayMetrics.density

        if (enableBackgroundDashEffect) mBackgroundStrokePaint.pathEffect = DashPathEffect(
            arrayOf(
                dashLineLength * displayMetrics.density, dashLineOffset * displayMetrics.density
            ).toFloatArray(), 1f
        )
        else mBackgroundStrokePaint.pathEffect = null

        options.fadeRepeatCount = DEFAULT_FADE_REPEAT_COUNT
        options.minFadeAlpha = DEFAULT_MIN_FADE_ALPHA
        mBackgroundStrokePaint.color = progressBackgroundColor
        mForegroundStrokePaint.color = progressColor
        dotPaint.color = dotColor
        mBackgroundStrokePaint.strokeWidth = progressBackgroundWidth
        mForegroundStrokePaint.strokeWidth = progressWidth
        dotPaint.strokeWidth = dotWidth
    }

    private fun setAnimators() {
        mProgressAnimator.setFloatValues(0f, progress.toFloat())
        mProgressAnimator.addUpdateListener { animation ->
            currentAnimatedProgress = animation.animatedValue.toString().toFloat()
            listener.invoke(animation)
            invalidate()
        }

        mAlphaAnimator.addUpdateListener { animation ->
            circularProgressBarAlpha = (abs(
                animation.animatedValue.toString().toFloat() * 2 / animationDuration - 1
            ) * (DEFAULT_ALPHA - minFadeAlpha) + minFadeAlpha).toInt()
            invalidate()
        }
    }


    fun setListener(action: (ValueAnimator) -> Unit): CircularProgressBar {
        this.listener = action
        return this
    }

    /**
     * Refresh view and set proper attribute values.
     * It is handled by default (whenever an attribute's value changes), no need to call it explicitly.
     */

    private fun refresh() {
        this.circularProgressBarAlpha = DEFAULT_ALPHA
        mBackgroundStrokePaint.color = progressBackgroundColor
        mForegroundStrokePaint.color = progressColor
        this.dotPaint.color = dotColor
        this.mBackgroundStrokePaint.strokeWidth = progressBackgroundWidth
        this.mForegroundStrokePaint.strokeWidth = progressWidth
        this.dotPaint.strokeWidth = dotWidth
        this.mRadius = (mSize - max(max(progressBackgroundWidth, progressWidth), dotWidth)) / 2.0f

        val displayMetrics = context.resources.displayMetrics
        if (enableBackgroundDashEffect) this.mBackgroundStrokePaint.pathEffect = DashPathEffect(
            arrayOf(
                dashLineLength * displayMetrics.density, dashLineOffset * displayMetrics.density
            ).toFloatArray(), 1f
        )
        else this.mBackgroundStrokePaint.pathEffect = null
        if (progress != currentAnimatedProgress.toInt() && !disableDefaultSweep) sweep()
        else {
            currentAnimatedProgress = progress.toFloat()
            invalidate()
        }
    }

    /**
     * Stop Animations and invalidate view
     */

    private fun stopAnimation() {
        this.circularProgressBarAlpha = DEFAULT_ALPHA
        currentAnimatedProgress = progress.toFloat()
        if (mProgressAnimator.isRunning) mProgressAnimator.cancel()
        if (mAlphaAnimator.isRunning) mAlphaAnimator.cancel()
        invalidate()
    }

    @SuppressLint("NewApi")
    fun sweep() {
        if (currentAnimatedProgress.toInt() == progress) currentAnimatedProgress = 0f
        mProgressAnimator.setFloatValues(currentAnimatedProgress, progress.toFloat())
        mProgressAnimator.duration = animationDuration.toLong()

        mProgressAnimator.interpolator = when (interpolator) {
            ACCELERATE -> AccelerateInterpolator()
            DECELERATE -> DecelerateInterpolator()
            else -> LinearInterpolator()
        }

        mProgressAnimator.start()
    }

    @SuppressLint("NewApi")
    fun fade() {
        currentAnimatedProgress = progress.toFloat()
        mAlphaAnimator.duration = animationDuration.toLong() //* fadeRepeatCount


        mAlphaAnimator.interpolator = when (interpolator) {
            ACCELERATE -> AccelerateInterpolator()
            DECELERATE -> DecelerateInterpolator()
            else -> LinearInterpolator()
        }
        mAlphaAnimator.setFloatValues(0f, animationDuration.toFloat())
        mAlphaAnimator.repeatCount = fadeRepeatCount
        mAlphaAnimator.start()
    }

    private fun setupOptionsAttrNull(displayMetrics: DisplayMetrics) = with(options) {
        progress = DEFAULT_PROGRESS
        maxProgress = DEFAULT_MAX_PROGRESS
        progressColor = DEFAULT_PROGRESS_COLOR
        progressWidth = DEFAULT_PROGRESS_WIDTH * displayMetrics.density
        progressBackgroundColor = DEFAULT_PROGRESS_BACKGROUND_COLOR
        progressBackgroundWidth = DEFAULT_PROGRESS_BACKGROUND_WIDTH * displayMetrics.density
        textColor = DEFAULT_TEXT_COLOR
        textSize = DEFAULT_TEXT_SIZE
        startAngle = DEFAULT_START_ANGLE
        dotWidth = DEFAULT_DOT_WIDTH * displayMetrics.density
        dotColor = DEFAULT_PROGRESS_COLOR
        enableBackgroundDashEffect = DEFAULT_ENABLE_BACKGROUND_DASH_EFFECT
        showDot = DEFAULT_SHOW_DOT
        progressCap = DEFAULT_PROGRESS_CAP
        animationDuration = DEFAULT_ANIMATION_DURATION
        interpolator = DEFAULT_INTERPOLATOR
    }

    private fun TypedArray.setupOptionsWithStyling(displayMetrics: DisplayMetrics) = with(options) {
        progress = getInt(R.styleable.CircularProgressBar_progressValue, DEFAULT_PROGRESS)
        maxProgress = getInt(
            R.styleable.CircularProgressBar_maxProgress,
            DEFAULT_MAX_PROGRESS
        )
        progressColor = getColor(
            R.styleable.CircularProgressBar_progressColor,
            DEFAULT_PROGRESS_COLOR
        )
        progressWidth = getDimension(
            R.styleable.CircularProgressBar_progressWidth,
            DEFAULT_PROGRESS_WIDTH * displayMetrics.density
        )

        progressBackgroundWidth = getDimension(
            R.styleable.CircularProgressBar_progressBackgroundWidth,
            DEFAULT_PROGRESS_BACKGROUND_WIDTH * displayMetrics.density
        )
        progressBackgroundColor = getInt(
            R.styleable.CircularProgressBar_progressBackgroundColor,
            DEFAULT_PROGRESS_BACKGROUND_COLOR
        )
        startAngle = getInteger(
            R.styleable.CircularProgressBar_startAngle, DEFAULT_START_ANGLE
        )
        enableBackgroundDashEffect = getBoolean(
            R.styleable.CircularProgressBar_enableBackgroundDashEffect,
            DEFAULT_ENABLE_BACKGROUND_DASH_EFFECT
        )
        showDot = getBoolean(R.styleable.CircularProgressBar_showDot, DEFAULT_SHOW_DOT)
        dotWidth = getDimension(
            R.styleable.CircularProgressBar_dotWidth,
            DEFAULT_DOT_WIDTH * displayMetrics.density
        )
        dotColor = getColor(R.styleable.CircularProgressBar_progressColor, DEFAULT_PROGRESS_COLOR)
        animationDuration = getInteger(
            R.styleable.CircularProgressBar_animationDuration, DEFAULT_ANIMATION_DURATION
        )
        progressCap = getInt(R.styleable.CircularProgressBar_progressCap, DEFAULT_PROGRESS_CAP)
        interpolator = getInt(R.styleable.CircularProgressBar_interpolator, DEFAULT_INTERPOLATOR)
    }

    companion object {
        const val ROUND = 0

        const val LINEAR = 0
        const val ACCELERATE = 1
        const val DECELERATE = 2

        private const val DEFAULT_PROGRESS = 25
        private const val DEFAULT_MAX_PROGRESS = 100
        private const val DEFAULT_PROGRESS_COLOR = Color.BLUE
        private const val DEFAULT_PROGRESS_WIDTH = 16f

        private const val DEFAULT_PROGRESS_BACKGROUND_WIDTH = 4f
        private const val DEFAULT_PROGRESS_BACKGROUND_COLOR = Color.LTGRAY

        private const val DEFAULT_TEXT_COLOR = Color.BLACK
        private const val DEFAULT_TEXT_SIZE = 14f

        private const val DEFAULT_START_ANGLE = 0

        private const val DEFAULT_ENABLE_BACKGROUND_DASH_EFFECT = false
        private const val DEFAULT_SHOW_DOT = true

        private const val DEFAULT_PROGRESS_CAP = ROUND

        private const val DEFAULT_DOT_WIDTH = 20f

        private const val DEFAULT_DASH_LINE_LENGTH = 8f
        private const val DEFAULT_DASH_LINE_OFFSET = 3f

        private const val DEFAULT_ANIMATION_DURATION = 1000
        private const val DEFAULT_INTERPOLATOR = LINEAR

        private const val DEFAULT_SIZE_DP = 128f

        private const val DEFAULT_ALPHA = 255
        private const val DEFAULT_FADE_REPEAT_COUNT = 3
        private const val DEFAULT_MIN_FADE_ALPHA = 32
    }
}