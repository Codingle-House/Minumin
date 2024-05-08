package id.co.minumin.presentation.pro

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.databinding.ActivityProBinding
import id.co.minumin.presentation.home.HomeActivity
import id.co.minumin.util.NestedScrollViewOverScrollDecorAdapter
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator

/**
 * Created by pertadima on 14,February,2021
 */

@AndroidEntryPoint
class ProActivity : BaseActivity<ActivityProBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityProBinding
        get() = ActivityProBinding::inflate


    private val proViewModel: ProViewModel by viewModels()

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        setupToolbar()
        initUi()
    }

    override fun onViewModelObserver() = Unit

    private fun setupToolbar() = binding.proToolbar.setOnClickListener {
        finish()
    }

    private fun initUi() {
        VerticalOverScrollBounceEffectDecorator(
            NestedScrollViewOverScrollDecorAdapter(binding.proScrollview)
        )
        binding.proButtonUpgrade.setOnClickListener {

        }
        binding.proButtonRestore.setOnClickListener {
            proViewModel.updatePurchaseStatus()
        }
    }

    override fun finish() {
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
        finishAffinity()
    }
}