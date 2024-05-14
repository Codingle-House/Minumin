package id.co.minumin.presentation.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.CupSelectionDto
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.data.dto.PhysicalActivitiesDto
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.dto.WeatherConditionDto
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.domain.repository.AppRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by pertadima on 10,February,2021
 */

@HiltViewModel
class MainViewModel @Inject constructor(
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

    private var liveDataWaterAndCupMerger = MutableLiveData<Pair<CupSelectionDto, Int>>()
    fun observeLiveDataWaterAndCupMerger(): LiveData<Pair<CupSelectionDto, Int>> =
        liveDataWaterAndCupMerger

    private val waterConsumption = MutableLiveData<List<DrinkDto>>()
    fun observeWaterConsumption(): LiveData<List<DrinkDto>> = waterConsumption

    private val customCupSize = MutableLiveData<Int>()
    fun observeCustomCupSize(): LiveData<Int> = customCupSize

    fun updateWeatherCondition(weatherConditionDto: WeatherConditionDto) {
        viewModelScope.launch {
            val weatherFlow = userPreferenceManager.updateWeatherCondition(weatherConditionDto)
            val userFlow = userPreferenceManager.getUserRegisterData()
            val physicalConditionFlow = userPreferenceManager.getPhysicalActivities()

            weatherFlow.combine(userFlow) { weather, user ->
                Pair(weather, user)
            }.combine(physicalConditionFlow) { params1, params2 ->
                Triple(params1.first, params1.second, params2)
            }.collect {
                weatherCondition.postValue(it.first)
                userConditionDto.postValue(it.second)
                physicalActivitiesDto.postValue(it.third)
                liveDataMerger.postValue(Triple(it.first, it.second, it.third))
            }
        }
    }

    fun updatePhysicalCondition(physicalActivities: PhysicalActivitiesDto) = viewModelScope.launch {
        val weatherFlow = userPreferenceManager.getWeatherCondition()
        val userFlow = userPreferenceManager.getUserRegisterData()
        val physicalConditionFlow =
            userPreferenceManager.updatePhysicalActivities(physicalActivities)

        weatherFlow.combine(userFlow) { weather, user ->
            Pair(weather, user)
        }.combine(physicalConditionFlow) { params1, params2 ->
            Triple(params1.first, params1.second, params2)
        }.collect {
            weatherCondition.postValue(it.first)
            userConditionDto.postValue(it.second)
            physicalActivitiesDto.postValue(it.third)
            liveDataMerger.postValue(Triple(it.first, it.second, it.third))
        }
    }

    fun getData() {
        viewModelScope.launch {
            val weatherFlow = userPreferenceManager.getWeatherCondition()
            val userFlow = userPreferenceManager.getUserRegisterData()
            val physicalConditionFlow = userPreferenceManager.getPhysicalActivities()

            weatherFlow.combine(userFlow) { weather, user -> Pair(weather, user) }
                .combine(physicalConditionFlow) { params1, params2 ->
                    Triple(params1.first, params1.second, params2)
                }.collect {
                    weatherCondition.postValue(it.first)
                    userConditionDto.postValue(it.second)
                    physicalActivitiesDto.postValue(it.third)
                    liveDataMerger.postValue(Triple(it.first, it.second, it.third))
                }
        }
        getCupSelection()
    }


    fun updateCupSelection(cupSelectionDto: CupSelectionDto) = viewModelScope.launch {
        val customCupSizeFlow = userPreferenceManager.getCustomCupSize()
        val cupSelectionFlow = userPreferenceManager.updateCupSelection(cupSelectionDto)

        cupSelectionFlow.combine(customCupSizeFlow) { cupSize, cupSelection ->
            Pair(cupSize, cupSelection)
        }.collect { liveDataWaterAndCupMerger.postValue(it) }
    }

    fun updateCustomCupSize(size: Int) = viewModelScope.launch {
        userPreferenceManager.updateCustomCupSize(size).collect { customCupSize.postValue(it) }
    }

    private fun getCupSelection() = viewModelScope.launch {
        val customCupSizeFlow = userPreferenceManager.getCustomCupSize()
        val cupSelectionFlow = userPreferenceManager.getCupSelection()

        cupSelectionFlow.combine(customCupSizeFlow) { cupSize, cupSelection ->
            Pair(cupSize, cupSelection)
        }.collect { liveDataWaterAndCupMerger.postValue(it) }
    }

    fun drinkWater(drinkDto: DrinkDto) {
        viewModelScope.launch {
            val userWaterConsumptionList = repository.doDrinkWaterAndGet(drinkDto)
            waterConsumption.postValue(userWaterConsumptionList)
        }
    }

    fun getDrinkWater(currentDate: Date) = viewModelScope.launch {
        val list = repository.getDrinkWater(currentDate)
        waterConsumption.postValue(list)
    }

    fun doEditDrinkWater(drinkDto: DrinkDto, currentDate: Date) {
        viewModelScope.launch {
            with(repository) {
                repository.doEditDrinkWater(drinkDto)
                val list = getDrinkWater(currentDate)
                waterConsumption.postValue(list)
            }
        }
    }
}