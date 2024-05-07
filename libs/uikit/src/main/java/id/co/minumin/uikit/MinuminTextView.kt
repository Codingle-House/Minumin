package id.co.minumin.uikit

import Minumin.R
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import id.co.minumin.core.ext.getColorCompat

/**
 * Created by pertadima on 26,January,2021
 */

class MinuminTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        const val UNDEFINED_COLOR = 0
    }

    private var code: Int = 0
    private var color: Int = 0
    private var xmlTextColor: Int = 0
    private var textColorDefined = false

    init {
        attrs?.let {
            defaultAttribute(it)

            // defStyleAttr & defStyleRes set 0 to avoid default value style
            context.obtainStyledAttributes(it, R.styleable.MinuminTextView, 0, 0).apply {
                color = getInteger(R.styleable.MinuminTextView_minumin_text_color, -1)
                code = getInteger(R.styleable.MinuminTextView_minumin_text_code, -1)
                setTextCodeAppearance(code)
                recycle()
            }
        }
    }

    private fun defaultAttribute(attrs: AttributeSet?) {
        val specificAttr = intArrayOf(android.R.attr.textColor)
        val typedArray = context.obtainStyledAttributes(attrs, specificAttr)
        xmlTextColor = typedArray.getColor(0, UNDEFINED_COLOR)
        textColorDefined = typedArray.hasValue(0)
        typedArray.recycle()
    }

    private fun applyAppearance(@StyleRes id: Int) {
        @Suppress
        TextViewCompat.setTextAppearance(this, id)

        // -1 will not set text color
        setMinuminTextColor(color)

        if (textColorDefined && xmlTextColor != UNDEFINED_COLOR) this.setTextColor(xmlTextColor)
        invalidate()
        requestLayout()
    }

    fun setTextCode(code: Int) {
        val mTextColors = textColors
        setTextCodeAppearance(code)
        setTextColor(mTextColors)
    }

    /**
     *  example : setminuminTextColor(TextColor.light)
     */
    fun setMinuminTextColor(minuminTextColor: Int) {
        val colors = when (minuminTextColor) {
            TextColor.light -> context.getColorCompat(R.color.light_textview)
            TextColor.dark -> context.getColorCompat(R.color.dark_textview)
            TextColor.error -> context.getColorCompat(R.color.error_textview)
            else -> return
        }

        setTextColor(colors)
    }

    private fun setTextCodeAppearance(code: Int) {
        applyAppearance(
            when (code) {
                TextCode.h1 -> R.style.MinuminTextView_H1
                TextCode.h2 -> R.style.MinuminTextView_H2
                TextCode.h3 -> R.style.MinuminTextView_H3
                TextCode.h4_B -> R.style.MinuminTextView_H4_B
                TextCode.h4_M -> R.style.MinuminTextView_H4_M
                TextCode.h5_B -> R.style.MinuminTextView_H5_B
                TextCode.h5_M -> R.style.MinuminTextView_H5_M
                TextCode.h6_B -> R.style.MinuminTextView_H6_B
                TextCode.h6_M -> R.style.MinuminTextView_H6_M
                TextCode.h7_B -> R.style.MinuminTextView_H7_B
                TextCode.h7_S -> R.style.MinuminTextView_H7_S
                TextCode.h7_R -> R.style.MinuminTextView_H7_R
                TextCode.p_a_i -> R.style.MinuminTextView_PA_I
                TextCode.p_a_r -> R.style.MinuminTextView_PA_R
                TextCode.p_a_s -> R.style.MinuminTextView_PA_S
                TextCode.p_a_b -> R.style.MinuminTextView_PA_B
                else -> R.style.MinuminTextView_PA_B
            }
        )
    }

}

object TextCode {
    const val h1 = 0
    const val h2 = 1
    const val h3 = 2
    const val h4_B = 3
    const val h4_M = 4
    const val h5_B = 5
    const val h5_M = 6
    const val h6_B = 7
    const val h6_M = 8
    const val h7_B = 9
    const val h7_S = 10
    const val h7_R = 11
    const val p_a_i = 16
    const val p_a_r = 17
    const val p_a_s = 18
    const val p_a_b = 19
}

object TextColor {
    const val light = 0
    const val dark = 1
    const val error = 2
}