package id.co.minumin.presentation.progress

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Interpolator
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.databinding.ActivityProgressBinding
import id.co.minumin.presentation.result.ResultActivity
import kotlin.math.pow

/**
 * Created by pertadima on 30,January,2021
 */

class ProgressActivity : BaseActivity<ActivityProgressBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityProgressBinding
        get() = ActivityProgressBinding::inflate

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        binding.progressImageviewMascot.doBounceAnimation {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, ResultActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
                finish()
            }, ACTIVITY_TRANSITION_DELAY)
        }
    }

    override fun onViewModelObserver() = Unit

    private fun View.doBounceAnimation(onAnimationEnd: () -> Unit) {
        val bounceInterpolator = Interpolator { v -> getPowOut(v, POW) }

        ObjectAnimator.ofFloat(
            this,
            BOUNCE_TRANSLATION_Y, 0F,
            TRANSLATION_Y, 0F
        ).apply {
            interpolator = bounceInterpolator
            startDelay = BOUNCE_ANIMATION_DELAY
            duration = BOUNCE_ANIMATION_DURATION
            repeatCount = BOUNCE_REPEAT_COUNT
            start()
        }

        val delayMillis = (BOUNCE_REPEAT_COUNT * BOUNCE_ANIMATION_DURATION) +
                BOUNCE_ANIMATION_DELAY + FADE_ANIMATION_DELAY
        Handler(Looper.getMainLooper()).postDelayed({ onAnimationEnd.invoke() }, delayMillis)
    }

    private fun getPowOut(elapsedTimeRate: Float, pow: Double): Float {
        return (1.toFloat() - (1 - elapsedTimeRate.toDouble()).pow(pow)).toFloat()
    }

    companion object {
        private const val BOUNCE_TRANSLATION_Y = "translationY"

        private const val BOUNCE_REPEAT_COUNT = 4
        private const val BOUNCE_ANIMATION_DURATION = 500L
        private const val BOUNCE_ANIMATION_DELAY = 0L

        private const val FADE_ANIMATION_DELAY = 300L
        private const val ACTIVITY_TRANSITION_DELAY = 300L

        private const val POW = 2.0
        private const val TRANSLATION_Y = 25F
    }
}