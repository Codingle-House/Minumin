package id.co.minumin.presentation.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.util.SingleLiveEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by pertadima on 28,February,2021
 */
@HiltViewModel
class AboutUsViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel() {

    private val purchaseStatus = SingleLiveEvent<Boolean>()
    fun observePurchaseStatus(): LiveData<Boolean> = purchaseStatus

    fun getPurchaseStatus() = viewModelScope.launch {
        userPreferenceManager.getPurchaseStatus().collect {
            purchaseStatus.postValue(it)
        }
    }
}