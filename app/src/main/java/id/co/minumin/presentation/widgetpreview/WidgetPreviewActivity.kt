package id.co.minumin.presentation.widgetpreview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseActivity
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.data.dto.PhysicalActivitiesDto
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.dto.WeatherConditionDto
import id.co.minumin.databinding.ActivityWidgetPreviewBinding
import id.co.minumin.util.DateTimeUtil
import id.co.minumin.util.NestedScrollViewOverScrollDecorAdapter
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator

/**
 * Created by pertadima on 22,February,2021
 */

@AndroidEntryPoint
class WidgetPreviewActivity : BaseActivity<ActivityWidgetPreviewBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityWidgetPreviewBinding
        get() = ActivityWidgetPreviewBinding::inflate

    private val widgetPreviewViewModel: WidgetPreviewViewModel by viewModels()

    private val currentDate by lazy {
        DateTimeUtil.getCurrentDate()
    }

    private var totalWaterConsumption: Int = 0
    private var currentWaterConsumption: Int = 0

    override fun onViewCreated() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(android.R.color.white)
        setupToolbar()
        initUi()
        loadData()
    }

    override fun onViewModelObserver() {
        with(widgetPreviewViewModel) {
            observeLiveDataMerger().onResult { handleMergedLiveData(it) }
            observeWaterConsumption().onResult { handleDrinkListLiveData(it) }
        }
    }

    private fun setupToolbar() {
        binding.widgetpreviewToolbar.setOnClickListener {
            finish()
        }
    }

    private fun initUi() {
        VerticalOverScrollBounceEffectDecorator(
            NestedScrollViewOverScrollDecorAdapter(binding.widgetpreviewScrollview)
        )

    }

    private fun loadData() {
        with(widgetPreviewViewModel) {
            getData()
            getDrinkWater(currentDate)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleMergedLiveData(
        liveData: Triple<WeatherConditionDto, UserRegisterDto, PhysicalActivitiesDto>
    ) {
        val userWaterConsumption: Int = liveData.second.waterNeeds
        val weatherConditionConsumption: Int = liveData.first.consumption
        val activitiesConsumption = liveData.third.consumption
        totalWaterConsumption =
            userWaterConsumption + weatherConditionConsumption + activitiesConsumption

        if (currentWaterConsumption != 0) {
            calculatePersentage()
        }
    }

    private fun handleDrinkListLiveData(list: List<DrinkDto>) {
        currentWaterConsumption = list.sumBy { data -> data.consumption }
        calculatePersentage()
    }

    @SuppressLint("SetTextI18n")
    private fun calculatePersentage() {
        val percentage =
            currentWaterConsumption.toDouble() / totalWaterConsumption.toDouble() * MAX_PERCENTAGE

        binding.widgetpreviewProgresbarProgress.progress = percentage.toInt()
        binding.widgetpreviewTextviewCurrent1.text =
            getString(R.string.general_placeholder_ml, currentWaterConsumption.toString())
        binding.widgetpreviewTextviewTotal1.text =
            getString(R.string.general_placeholder_ml, totalWaterConsumption.toString())

        binding.widgetpreviewTextviewCurrent2.text =
            getString(R.string.general_placeholder_ml, currentWaterConsumption.toString())
        binding.widgetpreviewTextviewTotal2.text =
            getString(R.string.general_placeholder_ml, totalWaterConsumption.toString())
    }

    companion object {
        private const val MAX_PERCENTAGE = 100
    }
}