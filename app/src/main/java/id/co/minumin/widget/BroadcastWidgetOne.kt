package id.co.minumin.widget

import android.app.PendingIntent
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
import id.co.minumin.presentation.pro.ProActivity
import id.co.minumin.util.DateTimeUtil
import id.co.minumin.util.DateTimeUtil.getCurrentDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by pertadima on 22,February,2021
 */

@AndroidEntryPoint
class BroadcastWidgetOne : AppWidgetProvider() {

    @Inject
    lateinit var userPreferenceManager: UserPreferenceManager

    @Inject
    lateinit var repository: AppRepository

    private val currentDate by lazy {
        getCurrentDate()
    }

    private val job = SupervisorJob()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        coroutineScope.launch {
            userPreferenceManager.getPurchaseStatus().collect {
                if (it) {
                    val views = RemoteViews(context.packageName, R.layout.widget_one)
                    val intent = Intent(context, BroadcastWidgetOne::class.java)
                    intent.action = ACTION_ADD
                    val pendingIntent = PendingIntent.getBroadcast(
                        context, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    views.setOnClickPendingIntent(R.id.widget_cardview_add_1, pendingIntent)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } else {
                    val views = RemoteViews(context.packageName, R.layout.widget_one)
                    val pendingIntent = Intent(context, ProActivity::class.java)
                    pendingIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    val contentIntent =
                        PendingIntent.getActivity(
                            context,
                            99,
                            pendingIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    views.setOnClickPendingIntent(R.id.widget_cardview_add_1, contentIntent)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
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
            coroutineScope.launch {
                updateData(context)
            }
        } else {
            coroutineScope.launch {
                loadData(context)
            }
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
                date = DateTimeUtil.convertDate(currentDate).orEmpty(),
                time = DateTimeUtil.getCurrentTime(),
                consumption = selectedCupCapacity
            )
            val total = repository.doDrinkWaterAndGet(drinkDto).sumBy { list -> list.consumption }


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
                repository.getDrinkWater(currentDate).sumBy { list -> list.consumption }

            updateWidget(context, waterNeeds, total)
        }
    }

    private fun updateWidget(context: Context, waterNeeds: Int, currentConsumption: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_one)

        views.setTextViewText(
            R.id.widget_textview_current_1,
            context.getString(R.string.general_placeholder_ml, currentConsumption.toString())
        )

        views.setTextViewText(
            R.id.widget_textview_total_1,
            context.getString(R.string.general_placeholder_ml, waterNeeds.toString())
        )

        val appWidget = ComponentName(
            context,
            BroadcastWidgetOne::class.java
        )

        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(appWidget, views)
    }

    companion object {
        private const val ACTION_ADD = "ACTION_ADD"
    }
}