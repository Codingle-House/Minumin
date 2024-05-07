package id.co.minumin.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.dto.UserNavigationDto
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.preference.UserPreferenceManager
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 01,February,2021
 */

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel() {
    fun updateUserRegisterData(userRegisterDto: UserRegisterDto) {
        viewModelScope.launch {
            with(userPreferenceManager) {
                registerUser(userRegisterDto)
                updateUserNavigation(UserNavigationDto.REGISTER)
            }
        }
    }
}