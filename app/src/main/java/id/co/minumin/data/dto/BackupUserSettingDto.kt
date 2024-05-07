package id.co.minumin.data.dto

/**
 * Created by pertadima on 16,February,2021
 */
data class BackupUserSettingDto(
    val id: Long = 0,
    val gender: String = "",
    val weight: Int = 0,
    val wakeUpTime: String = "",
    val sleepTime: String = "",
    val waterNeeds: Int = 0,
    val navigation: Int = 0,
    val language: Int = 0,
    val weatherCondition: Int = 0,
    val physicalCondition: Int = 0,
    val glassSelection: Int = 0,
    val isNotificationActive: Boolean = true
)