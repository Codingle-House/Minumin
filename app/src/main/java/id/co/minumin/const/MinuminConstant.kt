package id.co.minumin.const

/**
 * Created by pertadima on 30,January,2021
 */

object MinuminConstant {
    const val MAN = "MAN"
    const val WOMAN = "WOMAN"

    const val WATER_PER_WEIGHT = 30

    const val SUPPORT_EMAIL = "codingle.dev@gmail.com"
    const val SUPPORT_SUBJECT = "Minumin App Report"

    const val BACKUP_DRIVE = 0
    const val BACKUP_LOCAL = 1

    const val ACTION_BACKUP = 0
    const val ACTION_RESTORE = 1

    const val BACKUP_DB_NAME = "backupMinuminDb-%s.txt"
    const val SECRET_KEY = "e2561b314f55f1777d55d779a8af57e0"
    const val NOTIFICATION_FREQUENCY = 60L

    object CupSize {
        const val SIZE_100 = 100
        const val SIZE_150 = 150
        const val SIZE_200 = 200
        const val SIZE_300 = 300
        const val SIZE_400 = 400
    }

    object PhysicalActivities {
        const val ACTIVITY_NORMAL = 0
        const val ACTIVITY_ACTIVE = 330
        const val ACTIVITY_VERY_ACTIVE = 495
    }

    object WeatherConditions {
        const val WEATHER_NORMAL = 0
        const val WEATHER_WINTER = 165
        const val WEATHER_WARM = 247
        const val WEATHER_HOT = 412
    }

    object Time {
        const val MILLIS = 1000
        const val SECONDS = 60
        const val MINUTES = 60
        const val HOURS = 24
    }

    object ANGLE {
        const val ANGLE_90 = 90
        const val ANGLE_360 = 360
    }
}