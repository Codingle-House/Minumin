package id.co.minumin.presentation.home.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.domain.repository.AppRepository
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by pertadima on 12,February,2021
 */

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val repository: AppRepository
) : ViewModel() {

    private val waterConsumption = MutableLiveData<List<DrinkDto>>()
    fun observeWaterConsumption(): LiveData<List<DrinkDto>> = waterConsumption

    private val waterConsumptionInBetween = MutableLiveData<List<DrinkDto>>()
    fun observeWaterConsumptionInBetween(): LiveData<List<DrinkDto>> = waterConsumptionInBetween

    private val userConditionDto = MutableLiveData<UserRegisterDto>()
    fun observeUserCondition(): LiveData<UserRegisterDto> = userConditionDto

    init {
        getData()
    }

    fun getDrinkWater(currentDate: Date) = viewModelScope.launch {
        val list = repository.getDrinkWater(currentDate)
        waterConsumption.postValue(list)
    }


    fun doEditDrinkWater(drinkDto: DrinkDto, currentDate: Date) = viewModelScope.launch {
        with(repository) {
            repository.doEditDrinkWater(drinkDto)
            val list = getDrinkWater(currentDate)
            waterConsumption.postValue(list)
        }
    }

    fun getDrinkWaterBetweenDate(startDate: Date, endDate: Date) = viewModelScope.launch {
        val list = repository.getDrinkWaterBetweenDate(startDate, endDate)
        waterConsumptionInBetween.postValue(list)
    }

    private fun getData() = viewModelScope.launch {
        userPreferenceManager.getUserRegisterData().collect { userConditionDto.postValue(it) }
    }
}