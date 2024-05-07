package id.co.minumin.presentation.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.domain.repository.AppRepository
import id.co.minumin.util.SingleLiveEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 16,February,2021
 */

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val repository: AppRepository
) : ViewModel() {

    private val userConditionDto = MutableLiveData<UserRegisterDto>()
    fun observeUserCondition(): LiveData<UserRegisterDto> = userConditionDto

    private val purchaseStatus= SingleLiveEvent<Boolean>()
    fun observePurchaseStatus(): LiveData<Boolean> = purchaseStatus

    fun getUserData() = viewModelScope.launch {
        userPreferenceManager.getUserRegisterData().collect {
            userConditionDto.postValue(it)
        }
    }

    fun changeUserData(userRegisterDto: UserRegisterDto) = viewModelScope.launch {
        userPreferenceManager.registerUser(userRegisterDto)
        getUserData()
    }

    fun getPurchaseStatus() = viewModelScope.launch {
        userPreferenceManager.getPurchaseStatus().collect {
            purchaseStatus.postValue(it)
        }
    }
}