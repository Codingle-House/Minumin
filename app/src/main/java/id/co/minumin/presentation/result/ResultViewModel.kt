package id.co.minumin.presentation.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.UserNavigationDto
import id.co.minumin.data.dto.UserNavigationDto.RESULT
import id.co.minumin.data.preference.UserPreferenceManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 02,February,2021
 */

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel() {
    private val userWaterNeeds = MutableLiveData<Int>()
    fun observeWaterNeeds(): LiveData<Int> = userWaterNeeds

    init {
        updateUserNavigation()
    }

    fun getUserWaterNeeds() {
        viewModelScope.launch {
            userPreferenceManager.getUserRegisterData().collect {
                userWaterNeeds.postValue(it.waterNeeds)
            }
        }
    }

    private fun updateUserNavigation() {
        viewModelScope.launch {
            userPreferenceManager.updateUserNavigation(RESULT)
        }
    }
}