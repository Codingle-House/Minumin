package id.co.minumin.data.dto

import id.co.minumin.const.MinuminConstant.PhysicalActivities.ACTIVITY_ACTIVE
import id.co.minumin.const.MinuminConstant.PhysicalActivities.ACTIVITY_NORMAL
import id.co.minumin.const.MinuminConstant.PhysicalActivities.ACTIVITY_VERY_ACTIVE

/**
 * Created by pertadima on 11,February,2021
 */

enum class PhysicalActivitiesDto(val consumption: Int) {
    NORMAL(ACTIVITY_NORMAL),
    ACTIVE(ACTIVITY_ACTIVE),
    VERY_ACTIVE(ACTIVITY_VERY_ACTIVE)
}