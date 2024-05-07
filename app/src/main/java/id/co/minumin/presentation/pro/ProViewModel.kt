package id.co.minumin.presentation.pro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.preference.UserPreferenceManager
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 23,February,2021
 */

@HiltViewModel
class ProViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel() {
    fun updatePurchaseStatus() {
        viewModelScope.launch {
            userPreferenceManager.updatePurchaseStatus(true)
        }
    }
}