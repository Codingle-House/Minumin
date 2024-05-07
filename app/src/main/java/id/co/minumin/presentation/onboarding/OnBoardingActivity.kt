package id.co.minumin.presentation.onboarding

import android.content.Intent
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.databinding.ActivityOnboardingBinding
import id.co.minumin.presentation.register.RegisterActivity


/**
 * Created by pertadima on 27,January,2021
 */

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityOnboardingBinding
        get() = ActivityOnboardingBinding::inflate

    private val onBoardingViewModel: OnBoardingViewModel by viewModels()

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        setupMicroInteraction()
        setupActionListener()
    }

    override fun onViewModelObserver() {

    }

    private fun setupMicroInteraction() {
        binding.onboardingButtonRegister.animate().alpha(HIDE_VIEW).duration = 0L
        binding.onboardingImageviewHappy.animate().scaleX(HIDE_VIEW).scaleY(HIDE_VIEW)
            .setDuration(0L)
            .withEndAction {
                binding.onboardingImageviewHappy.animate().scaleX(FULL_VIEW).scaleY(FULL_VIEW)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DELAY)
                    .withEndAction {
                        binding.onboardingTextviewTitle.isVisible = true
                        binding.onboardingTextviewMessage.isVisible = true
                        val slideUp: Animation =
                            AnimationUtils.loadAnimation(this, R.anim.anim_slide_up)

                        with(binding.onboardingButtonRegister) {
                            startAnimation(slideUp)
                            animate().alpha(FULL_VIEW).duration = ANIMATION_DURATION
                        }
                    }
            }
    }

    private fun setupActionListener() {
        binding.onboardingButtonRegister.setOnClickListener {
            onBoardingViewModel.updateUserOnBoard()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
            finish()
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 600L
        private const val ANIMATION_DELAY = 200L

        private const val FULL_VIEW = 1F
        private const val HIDE_VIEW = 0F
    }
}