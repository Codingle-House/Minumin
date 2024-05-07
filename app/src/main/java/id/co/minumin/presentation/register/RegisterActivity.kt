package id.co.minumin.presentation.register

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.const.MinuminConstant
import id.co.minumin.const.MinuminConstant.MAN
import id.co.minumin.const.MinuminConstant.WOMAN
import id.co.minumin.core.ext.hideKeyboard
import id.co.minumin.core.ext.shakeAnimation
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.databinding.ActivityRegisterBinding
import id.co.minumin.presentation.progress.ProgressActivity

/**
 * Created by pertadima on 27,January,2021
 */

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityRegisterBinding
        get() = ActivityRegisterBinding::inflate

    private val registerViewModel: RegisterViewModel by viewModels()

    private var selectedGender: String? = null

    private val interactionHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var interactionRunnable = Runnable {
        this.hideKeyboard()
    }

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        setupGenderListener()
        setupSubmitListener()
    }

    override fun onViewModelObserver() {

    }

    private fun setupGenderListener() {
        binding.registerImageviewWoman.setOnClickListener {
            onGenderSelected(WOMAN)
        }

        binding.registerImageviewMan.setOnClickListener {
            onGenderSelected(MAN)
        }
    }

    private fun onGenderSelected(gender: String) {
        selectedGender = gender
        binding.registerImageviewCircleWoman.isInvisible = gender == MAN
        binding.registerImageviewCircleMan.isInvisible = gender == WOMAN
    }

    private fun setupSubmitListener() {
        binding.registerButtonSubmit.setOnClickListener {
            with(binding.registerTextviewError) {
                if (selectedGender == null) shakeAnimation() else clearAnimation()
                isGone = selectedGender != null
            }

            with(binding.registerFormWeight) {
                showError(getFormText().isEmpty())
            }

            with(binding.registerFormWakeuptime) {
                showError(getFormText().isEmpty())
            }

            with(binding.registerFormSleeptime) {
                showError(getFormText().isEmpty())
            }

            val weight = binding.registerFormWeight.getFormText()
            val wakeUpTime = binding.registerFormWakeuptime.getFormText()
            val sleepTime = binding.registerFormSleeptime.getFormText()

            if (selectedGender == null || weight.isEmpty() ||
                wakeUpTime.isEmpty() || sleepTime.isEmpty()
            ) return@setOnClickListener

            registerViewModel.updateUserRegisterData(
                UserRegisterDto(
                    gender = selectedGender.orEmpty(),
                    weight = weight.toInt(),
                    wakeUpTime = wakeUpTime,
                    sleepTime = sleepTime,
                    waterNeeds = weight.toInt() * MinuminConstant.WATER_PER_WEIGHT,
                    isNotificationActive = true
                )
            )
            val intent = Intent(this, ProgressActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
            finish()
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        with(interactionHandler) {
            removeCallbacks(interactionRunnable)
            postDelayed(interactionRunnable, USER_INACTIVITY)
        }
    }

    override fun onDestroy() {
        interactionHandler.removeCallbacks(interactionRunnable)
        super.onDestroy()
    }

    companion object {
        private const val USER_INACTIVITY = 2000L
    }
}