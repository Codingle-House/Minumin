package id.co.minumin.data.dto

/**
 * Created by pertadima on 09,February,2021
 */

sealed class AchievementStatusDto {
    object None : AchievementStatusDto()
    object Done : AchievementStatusDto()
    object Fail : AchievementStatusDto()
}