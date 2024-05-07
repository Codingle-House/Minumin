package id.co.minumin.data.dto

/**
 * Created by pertadima on 11,February,2021
 */

data class DrinkDto(
    val id: Long = 0,
    val consumption: Int = 0,
    val time: String = "",
    val date: String = "",
    val isDeleted: Boolean = false
)