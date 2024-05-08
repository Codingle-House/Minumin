package id.co.minumin.presentation.home.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.LanguageDto
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.domain.repository.AppRepository
import id.co.minumin.util.SingleLiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 13,February,2021
 */

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val repository: AppRepository
) : ViewModel() {

    private val userConditionDto = MutableLiveData<UserRegisterDto>()
    fun observeUserCondition(): LiveData<UserRegisterDto> = userConditionDto

    private val languageDto = MutableLiveData<LanguageDto>()
    fun observeLanguageDto(): LiveData<LanguageDto> = languageDto

    private val createBackupFileResult = SingleLiveEvent<Pair<Int, Boolean>>()
    fun observeBackupFileResult(): LiveData<Pair<Int, Boolean>> = createBackupFileResult

    private val restoreSuccess = MutableLiveData<Boolean>()
    fun observeRestoreSuccess(): LiveData<Boolean> = restoreSuccess

    private val languageSwitchDto = SingleLiveEvent<LanguageDto>()
    fun observeLanguageSwitchDto(): LiveData<LanguageDto> = languageSwitchDto

    fun getUserData() = viewModelScope.launch {
        userPreferenceManager.getUserRegisterData().collect {
            userConditionDto.postValue(it)
        }
    }

    fun changeUserData(userRegisterDto: UserRegisterDto) = viewModelScope.launch {
        userPreferenceManager.registerUser(userRegisterDto)
        getUserData()
    }

    fun changeLanguage(languageDto: LanguageDto) = viewModelScope.launch {
        userPreferenceManager.updateLanguageSelection(languageDto)
        userPreferenceManager.getLanguageSelection().collect {
            languageSwitchDto.postValue(it)
        }
    }

    fun getLanguageSelection() = viewModelScope.launch {
        userPreferenceManager.getLanguageSelection().collect {
            languageDto.postValue(it)
        }
    }

    fun doBackup(backupStatus: Int) = viewModelScope.launch {
        userPreferenceManager.getUserData().collect {
            repository.doBackup(it)
            createBackupFileResult.postValue(Pair(backupStatus, true))
        }
    }


    fun doRestoreUserSetting() = viewModelScope.launch {
        val backUpData = repository.getBackupData().first()
        userPreferenceManager.restoreUserSetting(backUpData)
        restoreSuccess.postValue(true)
    }
}