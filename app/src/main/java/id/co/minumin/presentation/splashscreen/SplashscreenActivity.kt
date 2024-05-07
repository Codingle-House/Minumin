package id.co.minumin.presentation.splashscreen

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Interpolator
import androidx.activity.viewModels
import androidx.core.view.isGone
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.data.dto.UserNavigationDto
import id.co.minumin.databinding.ActivitySplashScreenBinding
import id.co.minumin.presentation.home.HomeActivity
import id.co.minumin.presentation.onboarding.OnBoardingActivity
import id.co.minumin.presentation.register.RegisterActivity
import id.co.minumin.presentation.result.ResultActivity
import id.co.minumin.util.LocaleHelper
import javax.inject.Inject
import kotlin.math.pow


/**
 * Created by pertadima on 26,January,2021
 */

@AndroidEntryPoint
class SplashscreenActivity : BaseActivity<ActivitySplashScreenBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivitySplashScreenBinding
        get() = ActivitySplashScreenBinding::inflate

    private val splashScreenViewModel: SplashScreenViewModel by viewModels()

    @Inject
    lateinit var localeHelper: LocaleHelper

    override fun onViewCreated() {
        splashScreenViewModel.getLastSelectedLanguage()
    }

    override fun onViewModelObserver() {
        with(splashScreenViewModel) {
            observeIsUserNavigation().onResult { navigation ->
                val intent = when (navigation) {
                    UserNavigationDto.NONE -> Intent(
                        this@SplashscreenActivity,
                        OnBoardingActivity::class.java
                    )
                    UserNavigationDto.ON_BOARD -> Intent(
                        this@SplashscreenActivity,
                        RegisterActivity::class.java
                    )
                    UserNavigationDto.REGISTER -> Intent(
                        this@SplashscreenActivity,
                        ResultActivity::class.java
                    )
                    UserNavigationDto.RESULT -> Intent(
                        this@SplashscreenActivity,
                        HomeActivity::class.java
                    )
                    UserNavigationDto.UNRECOGNIZED -> Intent(
                        this@SplashscreenActivity,
                        OnBoardingActivity::class.java
                    )
                }
                startActivity(intent)
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
                finish()
            }

            observeLanguageSelection().onResult {
                val languageCode = localeHelper.convertLanguageDtoToCode(it)
                val config = localeHelper.changeLocale(this@SplashscreenActivity, languageCode)
                onConfigurationChanged(config)

                binding.splashscreenImageviewLogo.doBounceAnimation {
                    binding.splashscreenTextviewMotto.isGone = false
                    binding.splashscreenTextviewAppname.isGone = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        splashScreenViewModel.isOnBoarding()
                    }, ACTIVITY_TRANSITION_DELAY)
                }
            }
        }
    }

    private fun View.doBounceAnimation(onAnimationEnd: () -> Unit) {
        val bounceInterpolator: Interpolator = Interpolator { v ->
            getPowOut(
                v,
                POW
            )
        }

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        binding.splashscreenTextviewMotto.text = getString(R.string.splashscreen_text_motto)
        super.onConfigurationChanged(newConfig)
    }

    companion object {
        private const val BOUNCE_TRANSLATION_Y = "translationY"

        private const val BOUNCE_REPEAT_COUNT = 4
        private const val BOUNCE_ANIMATION_DURATION = 800L
        private const val BOUNCE_ANIMATION_DELAY = 200L

        private const val FADE_ANIMATION_DELAY = 300L
        private const val ACTIVITY_TRANSITION_DELAY = 800L

        private const val POW = 2.0
        private const val TRANSLATION_Y = 25F
    }
}