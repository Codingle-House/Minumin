package id.co.minumin.util

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by pertadima on 12,February,2021
 */
class TimeStampConverter {
    @SuppressLint("ConstantLocale")
    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        if (value != null) {
            try {
                return df.parse(value)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return null
    }

    @TypeConverter
    fun dateToTimestamp(value: Date?): String? {
        return if (value == null) null else df.format(value)
    }
}