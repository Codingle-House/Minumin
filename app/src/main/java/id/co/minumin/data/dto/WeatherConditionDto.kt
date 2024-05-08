package id.co.minumin.data.dto

import id.co.minumin.const.MinuminConstant.WeatherConditions.WEATHER_HOT
import id.co.minumin.const.MinuminConstant.WeatherConditions.WEATHER_NORMAL
import id.co.minumin.const.MinuminConstant.WeatherConditions.WEATHER_WARM
import id.co.minumin.const.MinuminConstant.WeatherConditions.WEATHER_WINTER

/**
 * Created by pertadima on 10,February,2021
 */

enum class WeatherConditionDto(val consumption: Int) {
    NORMAL(WEATHER_NORMAL),
    WINTER(WEATHER_WINTER),
    WARM(WEATHER_WARM),
    HOT(WEATHER_HOT)
}
