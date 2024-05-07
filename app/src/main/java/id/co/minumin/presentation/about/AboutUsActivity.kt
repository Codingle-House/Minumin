package id.co.minumin.presentation.about

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.databinding.ActivityAboutusBinding
import id.co.minumin.presentation.pro.ProActivity
import id.co.minumin.presentation.view.ProFeatureView.Action.Click
import id.co.minumin.presentation.view.ProFeatureView.Action.Close
import id.co.minumin.util.NestedScrollViewOverScrollDecorAdapter
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator

/**
 * Created by pertadima on 21,February,2021
 */

@AndroidEntryPoint
class AboutUsActivity : BaseActivity<ActivityAboutusBinding>() {

    private val aboutUsViewModel: AboutUsViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityAboutusBinding
        get() = ActivityAboutusBinding::inflate

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        setupToolbar()
        loadData()
        initUi()
    }

    override fun onViewModelObserver() {
        with(aboutUsViewModel) {
            observePurchaseStatus().onResult { handlePurchaseStatusLiveData(it) }
        }
    }

    private fun initUi() {
        VerticalOverScrollBounceEffectDecorator(
            NestedScrollViewOverScrollDecorAdapter(binding.aboutusScrollview)
        )

        with(binding.aboutusProview) {
            showWithAnimation(false)
            setListener { action ->
                when (action) {
                    Click -> {
                        val intent = Intent(context, ProActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    }

                    Close -> {

                    }
                }
            }
        }

        binding.aboutusTextviewFreepik.setOnClickListener {
            openUrl("https://www.flaticon.com/authors/freepik")
        }

        binding.aboutusTextviewBqln.setOnClickListener {
            openUrl("https://www.flaticon.com/authors/bqlqn")
        }

        binding.aboutusTextviewVectorsmarket.setOnClickListener {
            openUrl("https://www.flaticon.com/authors/vectors-market")
        }

        binding.aboutusTextviewPixelperfect.setOnClickListener {
            openUrl("https://www.flaticon.com/authors/pixel-perfect")
        }

        binding.aboutusTextviewSrip.setOnClickListener {
            openUrl("https://www.flaticon.com/authors/srip")
        }
    }

    private fun setupToolbar() {
        binding.aboutusToolbar.setOnClickListener { finish() }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    private fun handlePurchaseStatusLiveData(status: Boolean) {

    }


    private fun loadData() {
        aboutUsViewModel.getPurchaseStatus()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
    }
}