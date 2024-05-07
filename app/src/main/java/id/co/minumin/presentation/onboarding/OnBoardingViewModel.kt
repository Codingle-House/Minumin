package id.co.minumin.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.UserNavigationDto
import id.co.minumin.data.preference.UserPreferenceManager
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 31,January,2021
 */

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel() {

    fun updateUserOnBoard() {
        viewModelScope.launch {
            userPreferenceManager.updateUserNavigation(UserNavigationDto.ON_BOARD)
        }
    }
}