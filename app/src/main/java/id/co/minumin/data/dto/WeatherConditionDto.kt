package id.co.minumin.data.dto

/**
 * Created by pertadima on 10,February,2021
 */

enum class WeatherConditionDto(val consumption: Int) {
    NORMAL(0),
    WINTER(165),
    WARM(247),
    HOT(412)
}
