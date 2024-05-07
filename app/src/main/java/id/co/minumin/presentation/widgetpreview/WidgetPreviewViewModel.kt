package id.co.minumin.presentation.widgetpreview

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.*
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.domain.repository.AppRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by pertadima on 22,February,2021
 */

@HiltViewModel
class WidgetPreviewViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val repository: AppRepository
) : ViewModel() {
    private val weatherCondition = MutableLiveData<WeatherConditionDto>()
    private val userConditionDto = MutableLiveData<UserRegisterDto>()
    private val physicalActivitiesDto = MutableLiveData<PhysicalActivitiesDto>()

    private var liveDataMerger =
        MutableLiveData<Triple<WeatherConditionDto, UserRegisterDto, PhysicalActivitiesDto>>()

    fun observeLiveDataMerger(): LiveData<Triple<WeatherConditionDto, UserRegisterDto, PhysicalActivitiesDto>> =
        liveDataMerger

    private val waterConsumption = MutableLiveData<List<DrinkDto>>()
    fun observeWaterConsumption(): LiveData<List<DrinkDto>> = waterConsumption

    fun getData() {
        viewModelScope.launch {
            val weatherFlow = userPreferenceManager.getWeatherCondition()
            val userFlow = userPreferenceManager.getUserRegisterData()
            val physicalConditionFlow = userPreferenceManager.getPhysicalActivities()

            weatherFlow.combine(userFlow) { weather, user ->
                Pair(weather, user)
            }.combine(physicalConditionFlow) { params1, params2 ->
                Triple(params1.first, params1.second, params2)
            }.collect { it ->
                weatherCondition.postValue(it.first)
                userConditionDto.postValue(it.second)
                physicalActivitiesDto.postValue(it.third)

                val merger = zipLiveData(
                    weatherCondition,
                    userConditionDto,
                    physicalActivitiesDto
                )
                liveDataMerger = merger
            }
        }
    }

    fun getDrinkWater(currentDate: Date) = viewModelScope.launch {
        val list = repository.getDrinkWater(currentDate)
        waterConsumption.postValue(list)
    }

    private fun <A, B, C> zipLiveData(
        a: LiveData<A>,
        b: LiveData<B>,
        c: LiveData<C>
    ): MutableLiveData<Triple<A, B, C>> {
        return MediatorLiveData<Triple<A, B, C>>().apply {
            var lastA: A? = null
            var lastB: B? = null
            var lastC: C? = null

            fun update() {
                val localLastA = lastA
                val localLastB = lastB
                val localLastC = lastC
                if (localLastA != null && localLastB != null && localLastC != null) {
                    this.value = Triple(localLastA, localLastB, localLastC)
                }
            }

            addSource(a) {
                lastA = it
                update()
            }
            addSource(b) {
                lastB = it
                update()
            }
            addSource(c) {
                lastC = it
                update()
            }
        }
    }
}