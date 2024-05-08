package id.co.minumin.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DAY_OF_MONTH

/**
 * Created by pertadima on 11,February,2021
 */

object DateTimeUtil {
    fun getCurrentDate() = Date()

    fun getCurrentDateString(dateFormat: String = DEFAULT_DATE): String {
        return SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date())
    }

    fun getCurrentTime(dateFormat: String = DEFAULT_TIME): String {
        return SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date())
    }

    fun convertDate(
        date: Date,
        dateFormat: String = DEFAULT_DATE
    ): String? {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        return formatter.format(date)
    }

    fun convertDate(
        dateInMilliseconds: Long,
        dateFormat: String = DEFAULT_DATE
    ): String? {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        return formatter.format(Date(dateInMilliseconds))
    }

    fun convertDate(
        date: String,
        dateFormat: String = DEFAULT_DATE
    ): Date? {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        return formatter.parse(date)
    }

    fun getDaysAgo(daysAgo: Int = SEVEN_DAYS): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        return calendar.time
    }

    fun getListDaysAgo(daysAgo: Int = SEVEN_DAYS): List<Date> {
        val list = mutableListOf<Date>()
        for (i in 0 until daysAgo) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            list.add(calendar.time)
        }
        return list
    }

    fun getDayCurrentWeek(): List<Date> {
        val list = mutableListOf<Date>()
        val format: DateFormat = SimpleDateFormat(DEFAULT_DATE, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar[Calendar.DAY_OF_WEEK] = Calendar.MONDAY

        for (i in 0 until SEVEN_DAYS) {
            list.add(calendar.time)
            calendar.add(DAY_OF_MONTH, 1)
        }
        return list
    }

    fun changeDateTimeFormat(
        value: String,
        currentDateFormat: String,
        targetDateFormat: String
    ): String {
        val parser = SimpleDateFormat(currentDateFormat, Locale.getDefault())
        val formatter = SimpleDateFormat(targetDateFormat, Locale.getDefault())
        return formatter.format(parser.parse(value) ?: Date())
    }

    const val DEFAULT_DATE = "yyyy-MM-dd"
    const val DEFAULT_TIME = "HH:mm"
    const val DEFAULT_TIME_FULL = "HH:mm aaa"
    const val SEVEN_DAYS = 7
    const val THIRTY_DAYS = 30

    const val BACKUP_DATE = "yyyy-MM-dd|HH:mm aaa"
    const val FULL_DATE_FORMAT = "EEEE, dd MMMM yyyy"
}