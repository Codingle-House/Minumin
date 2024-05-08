package id.co.minumin.presentation.result

import android.content.Intent
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.const.MinuminConstant.NOTIFICATION_FREQUENCY
import id.co.minumin.databinding.ActivityResultBinding
import id.co.minumin.notifications.helper.AlarmHelper
import id.co.minumin.presentation.home.HomeActivity
import javax.inject.Inject

/**
 * Created by pertadima on 31,January,2021
 */

@AndroidEntryPoint
class ResultActivity : BaseActivity<ActivityResultBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityResultBinding
        get() = ActivityResultBinding::inflate

    private val resultViewModel: ResultViewModel by viewModels()

    @Inject
    lateinit var alarmHelper: AlarmHelper

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        resultViewModel.getUserWaterNeeds()
        setupMicroInteraction()
        setupListener()
        setupAlarm()
    }

    override fun onViewModelObserver() {
        resultViewModel.observeWaterNeeds().onResult {
            binding.resultTextviewWater.text =
                getString(R.string.general_text_ml_day, it.toString())
        }
    }

    private fun setupMicroInteraction() {
        binding.resultButtonNext.animate().alpha(HIDE_VIEW).duration = 0L
        binding.resultImageviewHappy.animate().scaleX(HIDE_VIEW).scaleY(HIDE_VIEW).setDuration(0L)
            .withEndAction {
                binding.resultImageviewHappy.animate().scaleX(FULL_VIEW).scaleY(FULL_VIEW)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DELAY)
                    .withEndAction {
                        binding.resultTextviewWater.isVisible = true
                        binding.resultTextviewTitle.isVisible = true
                        binding.resultTextviewMessage.isVisible = true
                        val slideUp: Animation =
                            AnimationUtils.loadAnimation(this, R.anim.anim_slide_up)

                        with(binding.resultButtonNext) {
                            startAnimation(slideUp)
                            animate().alpha(FULL_VIEW).duration = ANIMATION_DURATION
                        }
                    }
            }
    }

    private fun setupListener() {
        binding.resultButtonNext.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
            finish()
        }
    }

    private fun setupAlarm() {
        with(alarmHelper) {
            cancelAlarm(this@ResultActivity)
            setAlarm(this@ResultActivity, NOTIFICATION_FREQUENCY)
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 600L
        private const val ANIMATION_DELAY = 200L

        private const val FULL_VIEW = 1F
        private const val HIDE_VIEW = 0F
    }
}