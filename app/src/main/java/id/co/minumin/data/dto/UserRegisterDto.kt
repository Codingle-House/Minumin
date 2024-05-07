package id.co.minumin.data.dto

/**
 * Created by pertadima on 01,February,2021
 */

data class UserRegisterDto(
    val gender: String = "",
    val weight: Int = 0,
    val wakeUpTime: String = "",
    val sleepTime: String = "",
    val waterNeeds: Int = 0,
    val isNotificationActive: Boolean = true
)