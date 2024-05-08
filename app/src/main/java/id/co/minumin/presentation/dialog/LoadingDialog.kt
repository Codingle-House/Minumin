package id.co.minumin.presentation.dialog

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Interpolator
import id.co.minumin.databinding.DialogLoadingBinding
import id.co.minumin.uikit.MinuminDialog
import kotlin.math.pow

/**
 * Created by pertadima on 21,February,2021
 */

class LoadingDialog(context: Context) : MinuminDialog<DialogLoadingBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogLoadingBinding
        get() = DialogLoadingBinding::inflate

    override fun onCreateDialog() {
        binding.loadingImageviewMascot.doBounceAnimation()
    }


    private fun View.doBounceAnimation() {
        val bounceInterpolator = Interpolator { v -> getPowOut(v, POW) }

        ObjectAnimator.ofFloat(
            this,
            BOUNCE_TRANSLATION_Y, 0F,
            TRANSLATION_Y, 0F
        ).apply {
            interpolator = bounceInterpolator
            startDelay = BOUNCE_ANIMATION_DELAY
            duration = BOUNCE_ANIMATION_DURATION
            repeatCount = Animation.INFINITE
            start()
        }
    }

    private fun getPowOut(elapsedTimeRate: Float, pow: Double): Float {
        return (1.toFloat() - (1 - elapsedTimeRate.toDouble()).pow(pow)).toFloat()
    }

    companion object {

        private const val BOUNCE_TRANSLATION_Y = "translationY"
        private const val BOUNCE_ANIMATION_DURATION = 800L
        private const val BOUNCE_ANIMATION_DELAY = 0L

        private const val POW = 2.0
        private const val TRANSLATION_Y = 25F

        fun newInstance(context: Context) = LoadingDialog(context)
    }
}