package id.co.minumin.presentation.home.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseFragment
import id.co.minumin.core.DiffCallback
import id.co.minumin.core.DividerItemDecoration
import id.co.minumin.core.ext.getDrawableCompat
import id.co.minumin.data.dto.CupSelectionDto
import id.co.minumin.data.dto.CupSelectionDto.CUP_100
import id.co.minumin.data.dto.CupSelectionDto.CUP_150
import id.co.minumin.data.dto.CupSelectionDto.CUP_200
import id.co.minumin.data.dto.CupSelectionDto.CUP_300
import id.co.minumin.data.dto.CupSelectionDto.CUP_400
import id.co.minumin.data.dto.CupSelectionDto.CUP_CUSTOM
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.data.dto.PhysicalActivitiesDto
import id.co.minumin.data.dto.PhysicalActivitiesDto.ACTIVE
import id.co.minumin.data.dto.PhysicalActivitiesDto.VERY_ACTIVE
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.dto.WeatherConditionDto
import id.co.minumin.data.dto.WeatherConditionDto.HOT
import id.co.minumin.data.dto.WeatherConditionDto.NORMAL
import id.co.minumin.data.dto.WeatherConditionDto.WARM
import id.co.minumin.data.dto.WeatherConditionDto.WINTER
import id.co.minumin.databinding.FragmentMainBinding
import id.co.minumin.presentation.dialog.CupSelectionDialog
import id.co.minumin.presentation.dialog.InputCupDialog
import id.co.minumin.presentation.dialog.WeatherConditionDialog
import id.co.minumin.presentation.dialog.physicalactivities.PhysicalActivitiesDialog
import id.co.minumin.presentation.home.adapter.DrinkAdapter
import id.co.minumin.util.DateTimeUtil.FULL_DATE_FORMAT
import id.co.minumin.util.DateTimeUtil.convertDate
import id.co.minumin.util.DateTimeUtil.getCurrentDate
import id.co.minumin.util.DateTimeUtil.getCurrentDateString
import id.co.minumin.util.DateTimeUtil.getCurrentTime
import id.co.minumin.util.NestedScrollViewOverScrollDecorAdapter
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import java.util.*
import javax.inject.Inject


/**
 * Created by pertadima on 31,January,2021
 */

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainBinding
        get() = FragmentMainBinding::inflate

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var diffCallback: DiffCallback

    private val currentDate by lazy { getCurrentDate() }

    private val drinkAdapter by lazy {
        DrinkAdapter(requireContext(), diffCallback, itemListener = ::drinkItemListener)
    }

    private var selectedCupCapacity: Int = 0
    private var totalWaterConsumption: Int = 0
    private var currentWaterConsumption: Int = 0

    override fun onViewReady() {
        changeStatusBarTextColor(false)
        changeStatusBarColor(R.color.blue_primary)
        initUi()
        initRecyclerView()
        initHeaderListener()
        loadData()
    }

    override fun observeViewModel() = with(mainViewModel) {
        observeLiveDataMerger().onResult { handleMergedLiveData(it) }
        observeLiveDataWaterAndCupMerger().onResult {
            if (it.first != CUP_CUSTOM) {
                selectedCupCapacity = it.first.capacity
            } else {
                selectedCupCapacity = it.second
            }
            handleCupSelectionLiveData(it.first)
        }
        observeWaterConsumption().onResult { handleDrinkListLiveData(it) }
    }

    private fun initHeaderListener() = with(binding) {
        mainImageviewWeather.setOnClickListener {
            WeatherConditionDialog.newInstance(requireContext()).apply {
                setListener { mainViewModel.updateWeatherCondition(it) }
                show()
            }
        }

        mainImageviewActivities.setOnClickListener {
            PhysicalActivitiesDialog.newInstance(requireContext(), diffCallback).apply {
                setListener { mainViewModel.updatePhysicalCondition(it) }
                show()
            }
        }

        mainTextviewCupSelection.setOnClickListener {
            CupSelectionDialog.newInstance(requireContext()).apply {
                setListener {
                    if (it == CUP_CUSTOM) {
                        InputCupDialog.newInstance(context, selectedCupCapacity).apply {
                            setListener { size -> mainViewModel.updateCustomCupSize(size) }
                        }.show()
                    }
                    mainViewModel.updateCupSelection(it)
                }
                show()
            }
        }

        mainImageviewCup.setOnClickListener {
            val id = Calendar.getInstance().timeInMillis
            mainViewModel.drinkWater(
                DrinkDto(
                    id = id,
                    date = convertDate(currentDate).orEmpty(),
                    time = getCurrentTime(),
                    consumption = selectedCupCapacity
                )
            )
        }
    }

    private fun initUi() {
        VerticalOverScrollBounceEffectDecorator(
            NestedScrollViewOverScrollDecorAdapter(binding.mainScrollview)
        )
        binding.mainTextviewPercentage.text = "0%"
    }

    private fun initRecyclerView() {
        with(binding.mainRecyclerviewDrink) {
            adapter = drinkAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    R.drawable.general_view_divider
                )
            )
        }
    }

    private fun loadData() {
        binding.mainTextviewDate.text = getCurrentDateString(FULL_DATE_FORMAT)
        with(mainViewModel) {
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

        binding.mainImageviewWeather.setImageDrawable(
            context?.getDrawableCompat(
                when (liveData.first) {
                    NORMAL -> R.drawable.general_ic_weathernormal
                    WINTER -> R.drawable.general_ic_weatherwinter
                    WARM -> R.drawable.general_ic_weatherwarm
                    HOT -> R.drawable.general_ic_weatherhot
                }
            )
        )
        binding.mainImageviewActivities.setImageDrawable(
            context?.getDrawableCompat(
                when (liveData.third) {
                    PhysicalActivitiesDto.NORMAL -> R.drawable.general_ic_bodynormal
                    ACTIVE -> R.drawable.general_ic_bodyactive
                    VERY_ACTIVE -> R.drawable.general_ic_bodyextraactive
                }
            )
        )

        binding.mainTextviewConsumption.text = getString(
            R.string.home_text_water_consumption,
            currentWaterConsumption.toString(),
            totalWaterConsumption.toString()
        )

        if (currentWaterConsumption != 0) {
            calculatePercentage()
        }
    }

    private fun handleCupSelectionLiveData(cupSelectionDto: CupSelectionDto) {
        binding.mainImageviewCup.setImageDrawable(
            context?.getDrawableCompat(
                when (cupSelectionDto) {
                    CUP_100 -> R.drawable.general_ic_cupadd_100
                    CUP_150 -> R.drawable.general_ic_cupadd_150
                    CUP_200 -> R.drawable.general_ic_cupadd_200
                    CUP_300 -> R.drawable.general_ic_cupadd_300
                    CUP_400 -> R.drawable.general_ic_cupadd_400
                    CUP_CUSTOM -> R.drawable.general_ic_cupadd_custom
                }
            )
        )
    }

    private fun handleDrinkListLiveData(list: List<DrinkDto>) {
        currentWaterConsumption = list.sumOf { data -> data.consumption }
        drinkAdapter.setData(list)
        binding.mainPlaceholder.root.isGone = list.isNotEmpty()
        binding.mainRecyclerviewDrink.isGone = list.isEmpty()
        calculatePercentage()
    }

    @SuppressLint("SetTextI18n")
    private fun calculatePercentage() {
        val percentage =
            currentWaterConsumption.toDouble() / totalWaterConsumption.toDouble() * MAX_PERCENTAGE
        with(binding.mainProgress) {
            progress = percentage.toInt()
            setListener {
                binding.mainTextviewPercentage.text =
                    "${it.animatedValue.toString().toDouble().toInt()}%"
            }
        }
        binding.mainTextviewConsumption.text = getString(
            R.string.home_text_water_consumption,
            currentWaterConsumption.toString(),
            totalWaterConsumption.toString()
        )
        with(binding.mainTextviewMessage) {
            when {
                percentage.toInt() >= MAX_PERCENTAGE -> {
                    text = context.getString(R.string.home_text_message_done)
                    background = context.getDrawableCompat(R.drawable.home_bg_message_done)
                }

                percentage.toInt() >= MEDIAN_PERCENTAGE -> {
                    text = context.getString(R.string.home_text_message_orange)
                    background = context.getDrawableCompat(R.drawable.home_bg_message_orange)
                }

                else -> {
                    text = context.getString(R.string.home_text_message_red)
                    background = context.getDrawableCompat(R.drawable.home_bg_message_red)
                }
            }
        }
    }

    private fun drinkItemListener(action: DrinkAdapter.MenuAction) {
        when (action) {
            is DrinkAdapter.MenuAction.Delete -> {
                val newData = action.data.copy(
                    isDeleted = true
                )
                mainViewModel.doEditDrinkWater(newData, currentDate)
            }

            is DrinkAdapter.MenuAction.Edit -> {
                CupSelectionDialog.newInstance(requireContext()).apply {
                    setListener {
                        val newData = action.data.copy(
                            consumption = it.capacity
                        )
                        mainViewModel.doEditDrinkWater(newData, currentDate)
                    }
                    show()
                }
            }
        }
    }

    companion object {
        private const val MAX_PERCENTAGE = 100
        private const val MEDIAN_PERCENTAGE = 40
    }
}