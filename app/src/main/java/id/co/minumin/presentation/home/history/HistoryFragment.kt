package id.co.minumin.presentation.home.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.VERTICAL
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseFragment
import id.co.minumin.core.DiffCallback
import id.co.minumin.core.DividerItemDecoration
import id.co.minumin.core.ext.changeIconColor
import id.co.minumin.core.ext.convertDateFormat
import id.co.minumin.core.ext.getDrawableCompat
import id.co.minumin.data.dto.AchievementDto
import id.co.minumin.data.dto.AchievementStatusDto
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.data.dto.WaterConsumptionDto
import id.co.minumin.databinding.FragmentHistoryBinding
import id.co.minumin.databinding.ViewSinglecalendarBinding
import id.co.minumin.presentation.dialog.CupSelectionDialog
import id.co.minumin.presentation.home.adapter.AchievementAdapter
import id.co.minumin.presentation.home.adapter.DrinkAdapter
import id.co.minumin.presentation.view.CustomChartMarkerView
import id.co.minumin.uikit.TextColor
import id.co.minumin.util.DateTimeUtil.SEVEN_DAYS
import id.co.minumin.util.DateTimeUtil.THIRTY_DAYS
import id.co.minumin.util.DateTimeUtil.convertDate
import id.co.minumin.util.DateTimeUtil.getCurrentDate
import id.co.minumin.util.DateTimeUtil.getDayCurrentWeek
import id.co.minumin.util.DateTimeUtil.getDaysAgo
import id.co.minumin.util.DateTimeUtil.getListDaysAgo
import id.co.minumin.util.NestedScrollViewOverScrollDecorAdapter
import id.co.minumin.util.addAdditionalEffect
import id.co.minumin.util.getBarDataSetAdditionalEffect
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import java.util.*
import java.util.concurrent.TimeUnit.DAYS
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

/**
 * Created by pertadima on 31,January,2021
 */

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHistoryBinding
        get() = FragmentHistoryBinding::inflate

    private val historyViewModel: HistoryViewModel by viewModels()

    @Inject
    lateinit var diffCallback: DiffCallback

    private val customMarkerView by lazy {
        CustomChartMarkerView(requireContext(), unit = getString(R.string.general_text_ml))
    }

    private val drinkAdapter by lazy {
        DrinkAdapter(
            requireContext(),
            diffCallback,
            itemListener = ::drinkItemListener
        )
    }


    private val achievementAdapter by lazy {
        AchievementAdapter(requireContext(), diffCallback)
    }

    private val todayDate by lazy { getCurrentDate() }

    private val sevenDaysAgo by lazy { getDaysAgo(SEVEN_DAYS) }

    private val thirtyDaysAgo by lazy { getDaysAgo(THIRTY_DAYS) }

    private val sevenDaysAgoDateList by lazy {
        getListDaysAgo(SEVEN_DAYS).map {
            WaterConsumptionDto(convertDate(it).orEmpty())
        }
    }

    private val thirtyDaysAgoDateList by lazy {
        getListDaysAgo(THIRTY_DAYS).map {
            WaterConsumptionDto(convertDate(it).orEmpty())
        }
    }

    private val getDaysCurrentWeek by lazy {
        getDayCurrentWeek().map { WaterConsumptionDto(convertDate(it).orEmpty()) }
    }

    private var selectedDayFrame: Int = SEVEN_DAYS
    private var selectedDate: Date? = null
    private var userWaterNeed: Int = 0

    override fun onViewReady() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(R.color.white)
        initUi()
        initDayFrameListener()
        initRecyclerView()
        initBarChart()
        initSingleCalendar()
        loadData()
    }

    override fun observeViewModel() {
        with(historyViewModel) {
            observeWaterConsumption().onResult { list -> handleDrinkListLiveData(list) }
            observeWaterConsumptionInBetween().onResult { handleDrinkListLiveDataByDate(it) }
            observeUserCondition().onResult { userWaterNeed = it.waterNeeds }
        }
    }

    private fun loadData() {
        historyViewModel.getDrinkWaterBetweenDate(sevenDaysAgo, todayDate)
    }

    private fun initUi() {
        binding.historyTextviewDayframe.text =
            getString(R.string.history_placeholder_dayframe, selectedDayFrame.toString())
        VerticalOverScrollBounceEffectDecorator(
            NestedScrollViewOverScrollDecorAdapter(binding.historyScrollview)
        )
    }

    private fun initDayFrameListener() {
        binding.historyImageviewLeft.setOnClickListener {
            if (selectedDayFrame != SEVEN_DAYS) {
                selectedDayFrame = SEVEN_DAYS
                binding.historyTextviewDayframe.text =
                    getString(R.string.history_placeholder_dayframe, selectedDayFrame.toString())
                binding.historyImageviewLeft.changeIconColor(R.color.blue_inactive)
                binding.historyImageviewRight.changeIconColor(R.color.white)
                historyViewModel.getDrinkWaterBetweenDate(sevenDaysAgo, todayDate)
            }
        }

        binding.historyImageviewRight.setOnClickListener {
            if (selectedDayFrame != THIRTY_DAYS) {
                selectedDayFrame = THIRTY_DAYS
                binding.historyTextviewDayframe.text =
                    getString(
                        R.string.history_placeholder_dayframe,
                        selectedDayFrame.toString()
                    )
                binding.historyImageviewLeft.changeIconColor(R.color.white)
                binding.historyImageviewRight.changeIconColor(R.color.blue_inactive)
                historyViewModel.getDrinkWaterBetweenDate(thirtyDaysAgo, todayDate)
            }
        }
    }

    private fun initRecyclerView() {
        with(binding.historyRecyclerviewDrink) {
            adapter = drinkAdapter.apply {
                setHasStableIds(true)
            }
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    R.drawable.general_view_divider
                )
            )
        }

        with(binding.historyRecyclerviewAchievement) {
            adapter = achievementAdapter
            layoutManager =
                GridLayoutManager(requireContext(), SPAN_COUNT, VERTICAL, false)
        }
    }

    private fun initBarChart() {
        binding.historyChart.addAdditionalEffect(customMarkerView, MIN_CHART_VALUE)
    }

    private fun setBarData(studyTimeListDto: List<WaterConsumptionDto>) {
        val barEntries = studyTimeListDto.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.consumption.toFloat())
        }

        val barDataSet = requireContext().getBarDataSetAdditionalEffect(
            barEntries,
            R.color.green_chart_start,
            R.color.green_chart_end
        )
        val barData = BarData(listOf(barDataSet) as List<IBarDataSet>).apply {
            setValueTextSize(DEFAULT_TEXT_SIZE)
            barWidth = DEFAULT_BAR_WIDTH
        }
        setChartView(studyTimeListDto, barData)
    }

    private fun setChartView(waterConsumptionListDto: List<WaterConsumptionDto>, barData: BarData) {
        with(binding.historyChart) {
            animateY(ANIMATION_DURATION)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return convertDateFormat(
                        CURRENT_CHART_DATE_FORMAT,
                        if (waterConsumptionListDto.size < value) value.toString()
                        else waterConsumptionListDto[value.toInt()].date,
                        DESIRED_CHART_FATE_FORMAT
                    )
                }
            }

            notifyDataSetChanged()
            data = barData
            invalidate()
        }
    }

    private fun initSingleCalendar() {
        val myCalendarViewManager = object : CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int = R.layout.view_singlecalendar

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                val binding = ViewSinglecalendarBinding.bind(holder.itemView)
                val textColor = if (isSelected) TextColor.light else TextColor.dark

                with(binding.singlecalendarTextviewDate) {
                    text = DateUtils.getDayNumber(date)
                    setMinuminTextColor(textColor)
                }
                with(binding.singlecalendarTextviewMonth) {
                    text = DateUtils.getMonth3LettersName(date)
                    setMinuminTextColor(textColor)
                }

                binding.singlecalendarLinearContainer.background =
                    context?.getDrawableCompat(
                        if (isSelected) R.drawable.general_shape_circle_blue else R.drawable.general_shape_circle_white
                    )
            }
        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date) = true
        }

        val myCalendarChangesObserver = object : CalendarChangesObserver {
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)

                val diff = sevenDaysAgo.time - date.time
                val daysDifferent = DAYS.convert(diff, MILLISECONDS)

                if (daysDifferent > 0) {
                    if (isSelected) {
                        selectedDate = date
                        historyViewModel.getDrinkWater(selectedDate ?: Date())
                    }
                } else {
                    if (isSelected) {
                        selectedDate = date
                        historyViewModel.getDrinkWater(selectedDate ?: Date())
                    }
                }
            }
        }


        binding.historySingleRowCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarSelectionManager = mySelectionManager
            calendarChangesObserver = myCalendarChangesObserver
            init()
            select(LAST_DATE)
        }
    }

    private fun handleDrinkListLiveData(list: List<DrinkDto>) {
        drinkAdapter.setData(list)
        binding.historyPlaceholder.root.isGone = list.isNotEmpty()
        with(binding.historyRecyclerviewDrink) {
            isGone = list.isEmpty()
            if (list.isNotEmpty()) scheduleLayoutAnimation()
        }
    }

    private fun handleDrinkListLiveDataByDate(list: List<DrinkDto>) {
        val chartList = list.groupBy { data -> data.date }.map { map ->
            WaterConsumptionDto(map.key, map.value.sumOf { sum -> sum.consumption })
        }

        val timeFrameList =
            if (selectedDayFrame == SEVEN_DAYS) sevenDaysAgoDateList else thirtyDaysAgoDateList

        val result = (timeFrameList + chartList)
            .groupingBy { group -> group.date }
            .reduce { _, accumulator: WaterConsumptionDto, element: WaterConsumptionDto ->
                accumulator.copy(consumption = element.consumption)
            }.values.toList().reversed()
            .takeLast(
                if (selectedDayFrame == SEVEN_DAYS) {
                    SEVEN_DAYS
                } else {
                    THIRTY_DAYS
                }
            )
        setBarData(result)
        val totalWater = result.sumOf { it.consumption }
        binding.historyTextviewTotalWater.text =
            getString(R.string.general_placeholder_ml, totalWater.toString())
        binding.historyTextviewAverageWater.text = getString(
            R.string.general_placeholder_ml,
            (totalWater / selectedDayFrame).toString()
        )


        if (achievementAdapter.itemCount != 0) return
        handleAchievementList(chartList)
    }

    private fun handleAchievementList(chartList: List<WaterConsumptionDto>) {
        val achievementResult = (getDaysCurrentWeek + chartList)
            .groupingBy { group -> group.date }
            .reduce { _, accumulator: WaterConsumptionDto, element: WaterConsumptionDto ->
                accumulator.copy(consumption = element.consumption)
            }.values.toList().reversed()
            .takeLast(SEVEN_DAYS).map {
                val achievementDate = convertDate(it.date)
                val status = when {
                    (achievementDate?.compareTo(todayDate) ?: 0) > 0 -> AchievementStatusDto.None
                    userWaterNeed > it.consumption -> AchievementStatusDto.Fail
                    else -> AchievementStatusDto.Done
                }
                val date = convertDate(it.date)
                AchievementDto(status = status, day = DateUtils.getDay3LettersName(date ?: Date()))
            }.reversed()
        binding.historyCardAchievement.isGone = false
        achievementAdapter.setData(achievementResult)
    }

    private fun drinkItemListener(action: DrinkAdapter.MenuAction) {
        when (action) {
            is DrinkAdapter.MenuAction.Delete -> {
                val newData = action.data.copy(
                    isDeleted = true
                )
                historyViewModel.doEditDrinkWater(newData, selectedDate ?: Date())
            }

            is DrinkAdapter.MenuAction.Edit -> {
                CupSelectionDialog.newInstance(requireContext()).apply {
                    setListener {
                        val newData = action.data.copy(
                            consumption = it.capacity
                        )
                        historyViewModel.doEditDrinkWater(newData, selectedDate ?: Date())
                    }
                    show()
                }
            }
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 1500
        private const val DEFAULT_TEXT_SIZE = 10F
        private const val DEFAULT_BAR_WIDTH = 0.7F
        private const val MIN_CHART_VALUE = 0F
        private const val CURRENT_CHART_DATE_FORMAT = "yyyy-MM-dd"
        private const val DESIRED_CHART_FATE_FORMAT = "dd/MM"
        private const val LAST_DATE = 365
        private const val SPAN_COUNT = 7
    }
}