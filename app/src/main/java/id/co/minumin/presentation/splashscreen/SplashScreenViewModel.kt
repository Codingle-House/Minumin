package id.co.minumin.presentation.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.LanguageDto
import id.co.minumin.data.dto.UserNavigationDto
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.util.SingleLiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 31,January,2021
 */

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel() {
    private val userNavigation = MutableLiveData<UserNavigationDto>()
    fun observeIsUserNavigation(): LiveData<UserNavigationDto> = userNavigation

    private val languageSelection = SingleLiveEvent<LanguageDto>()
    fun observeLanguageSelection(): LiveData<LanguageDto> = languageSelection

    init {
        viewModelScope.launch {
            // To be able access all features
            with(userPreferenceManager) {
                updatePurchaseStatus(true)
                getLanguageSelection().collect { languageSelection.postValue(it) }
            }
        }
    }

    fun isOnBoarding() = viewModelScope.launch {
        userPreferenceManager.getUserNavigation().collect { userNavigation.postValue(it) }
    }
}