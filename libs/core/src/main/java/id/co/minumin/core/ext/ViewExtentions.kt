package id.co.minumin.core.ext

import Minumin.R
import android.view.View
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.graphics.drawable.DrawableCompat.setTint
import androidx.core.graphics.drawable.DrawableCompat.wrap

/**
 * Created by pertadima on 30,January,2021
 */

fun View.shakeAnimation() {
    val shakeAnim = loadAnimation(context, R.anim.core_shake_animation)
    startAnimation(shakeAnim)
}

fun ImageView.changeIconColor(@ColorRes color: Int) = setTint(
    wrap(drawable),
    context.getColorCompat(color)
)
