package id.co.minumin.presentation.notification

import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.databinding.ActivityNotificationBinding
import id.co.minumin.util.DateTimeUtil.DEFAULT_TIME
import id.co.minumin.util.DateTimeUtil.DEFAULT_TIME_FULL
import id.co.minumin.util.DateTimeUtil.changeDateTimeFormat
import id.co.minumin.util.NestedScrollViewOverScrollDecorAdapter
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator

/**
 * Created by pertadima on 09,February,2021
 */

@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityNotificationBinding
        get() = ActivityNotificationBinding::inflate

    private val notificationViewModel: NotificationViewModel by viewModels()

    private var userRegisterDto: UserRegisterDto? = null

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        setupToolbar()
        initUi()
        loadData()
    }

    override fun onViewModelObserver() = with(notificationViewModel) {
        observeUserCondition().onResult { handleUserConditionLiveData(it) }
    }

    private fun setupToolbar() {
        binding.notificationToolbar.setOnClickListener { finish() }
    }

    private fun initUi() {
        VerticalOverScrollBounceEffectDecorator(
            NestedScrollViewOverScrollDecorAdapter(binding.notificationScrollview)
        )

        binding.notificationSwitchStatus.setOnCheckedChangeListener { _, b ->
            val newUserRegisterDto = userRegisterDto?.copy(isNotificationActive = b)
            newUserRegisterDto?.let {
                notificationViewModel.changeUserData(it)
            }
        }
    }

    private fun loadData() {
        with(notificationViewModel) {
            getUserData()
            getPurchaseStatus()
        }
    }

    private fun handleUserConditionLiveData(userRegisterDto: UserRegisterDto) {
        this.userRegisterDto = userRegisterDto
        val wakeupTime = changeDateTimeFormat(
            userRegisterDto.wakeUpTime,
            DEFAULT_TIME,
            DEFAULT_TIME_FULL
        )

        val sleepTime = changeDateTimeFormat(
            userRegisterDto.sleepTime,
            DEFAULT_TIME,
            DEFAULT_TIME_FULL
        )
        binding.notificationTextviewTime.text = "$wakeupTime - $sleepTime"
        binding.notificationSwitchStatus.isChecked = userRegisterDto.isNotificationActive
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
    }
}