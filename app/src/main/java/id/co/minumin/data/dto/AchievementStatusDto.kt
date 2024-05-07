package id.co.minumin.data.dto

/**
 * Created by pertadima on 09,February,2021
 */

sealed class AchievementStatusDto {
    data object None : AchievementStatusDto()
    data object Done : AchievementStatusDto()
    data object Fail : AchievementStatusDto()
}