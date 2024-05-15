package id.co.minumin.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.domain.repository.AppRepository
import id.co.minumin.util.DateTimeUtil
import id.co.minumin.util.DateTimeUtil.convertDate
import id.co.minumin.util.DateTimeUtil.getCurrentTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by pertadima on 22,February,2021
 */

@AndroidEntryPoint
class BroadcastWidgetTwo : AppWidgetProvider() {

    @Inject
    lateinit var userPreferenceManager: UserPreferenceManager

    @Inject
    lateinit var repository: AppRepository

    private val currentDate by lazy {
        DateTimeUtil.getCurrentDate()
    }

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        coroutineScope.launch {
            userPreferenceManager.getPurchaseStatus().collect {
                val views = RemoteViews(context.packageName, R.layout.widget_two)
                val intent = Intent(context, BroadcastWidgetTwo::class.java)
                intent.action = ACTION_ADD
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    FLAG_UPDATE_CURRENT
                )

                views.setOnClickPendingIntent(R.id.widget_cardview_add_2, pendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_ADD == intent.action) {
            coroutineScope.launch { updateData(context) }
        } else {
            coroutineScope.launch { loadData(context) }
        }
    }

    private suspend fun updateData(context: Context) {
        val weatherFlow = userPreferenceManager.getWeatherCondition()
        val userFlow = userPreferenceManager.getUserRegisterData()
        val physicalConditionFlow = userPreferenceManager.getPhysicalActivities()
        val cupSelectionFlow = userPreferenceManager.getCupSelection()

        weatherFlow.combine(userFlow) { weather, user ->
            Pair(weather, user)
        }.combine(physicalConditionFlow) { params1, params2 ->
            Triple(params1.first, params1.second, params2)
        }.combine(cupSelectionFlow) { params1, params2 ->
            Pair(params1, params2)
        }.collect {
            val waterNeeds =
                it.first.first.consumption + it.first.second.waterNeeds + it.first.third.consumption
            val selectedCupCapacity = it.second.capacity
            val id = Calendar.getInstance().timeInMillis
            val drinkDto = DrinkDto(
                id = id,
                date = convertDate(currentDate).orEmpty(),
                time = getCurrentTime(),
                consumption = selectedCupCapacity
            )
            val total = repository.doDrinkWaterAndGet(drinkDto).sumOf { list -> list.consumption }
            updateWidget(context, waterNeeds, total)
        }
    }

    private suspend fun loadData(context: Context) {
        val weatherFlow = userPreferenceManager.getWeatherCondition()
        val userFlow = userPreferenceManager.getUserRegisterData()
        val physicalConditionFlow = userPreferenceManager.getPhysicalActivities()

        weatherFlow.combine(userFlow) { weather, user ->
            Pair(weather, user)
        }.combine(physicalConditionFlow) { params1, params2 ->
            Triple(params1.first, params1.second, params2)
        }.collect {
            val waterNeeds =
                it.first.consumption + it.second.waterNeeds + it.third.consumption
            val total =
                repository.getDrinkWater(currentDate).sumOf { list -> list.consumption }

            updateWidget(context, waterNeeds, total)
        }
    }

    private fun updateWidget(context: Context, waterNeeds: Int, currentConsumption: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_two)

        val percentage =
            currentConsumption.toDouble() / waterNeeds.toDouble() * MAX_PERCENTAGE

        views.setTextViewText(
            R.id.widget_textview_current_2,
            context.getString(R.string.general_placeholder_ml, currentConsumption.toString())
        )

        views.setTextViewText(
            R.id.widget_textview_total_2,
            context.getString(R.string.general_placeholder_ml, waterNeeds.toString())
        )
        views.setProgressBar(
            R.id.widget_progresbar_progress,
            MAX_PERCENTAGE,
            percentage.toInt(),
            false
        )
        val appWidget = ComponentName(
            context,
            BroadcastWidgetTwo::class.java
        )

        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(appWidget, views)
    }

    companion object {
        private const val ACTION_ADD = "ACTION_WIDGET_ADD"
        private const val MAX_PERCENTAGE = 100
    }
}