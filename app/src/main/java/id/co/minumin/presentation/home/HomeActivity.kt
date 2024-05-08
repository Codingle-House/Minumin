package id.co.minumin.presentation.home

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.databinding.ActivityHomeBinding
import id.co.minumin.notifications.helper.AlarmHelper
import id.co.minumin.notifications.helper.NotificationHelper
import id.co.minumin.presentation.home.history.HistoryFragment
import id.co.minumin.presentation.home.main.MainFragment
import id.co.minumin.presentation.home.settings.SettingsFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityHomeBinding
        get() = ActivityHomeBinding::inflate

    @Inject
    lateinit var alarmHelper: AlarmHelper

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    addFragment(MainFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_history -> {
                    addFragment(HistoryFragment())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_settings -> {
                    addFragment(SettingsFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        showNotification()
    }

    override fun onViewModelObserver() {
        with(binding.homeBottomnavigation) {
            setOnNavigationItemReselectedListener {}
            setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
            onNavigationItemSelectedListener.onNavigationItemSelected(menu.findItem(R.id.navigation_home))
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.home_framelayout_content,
                fragment,
                fragment.javaClass.simpleName
            ).commit()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showNotification() {
        GlobalScope.launch(IO) {
            val nBuilder = notificationHelper.getNotificationCustom(true)
            notificationHelper.notify(1, nBuilder)
        }
    }
}