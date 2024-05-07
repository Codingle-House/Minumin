package id.co.minumin.data.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by pertadima on 16,February,2021
 */

@Entity(tableName = "tbl_backup")
data class BackupUserSettingEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "gender")
    var gender: String = "",
    @ColumnInfo(name = "weight")
    var weight: Int = 0,
    @ColumnInfo(name = "wakeUpTime")
    var wakeUpTime: String = "",
    @ColumnInfo(name = "sleepTime")
    var sleepTime: String = "",
    @ColumnInfo(name = "waterNeeds")
    var waterNeeds: Int = 0,
    @ColumnInfo(name = "navigation")
    var navigation: Int = 0,
    @ColumnInfo(name = "language")
    var language: Int = 0,
    @ColumnInfo(name = "weatherCondition")
    var weatherCondition: Int = 0,
    @ColumnInfo(name = "physicalCondition")
    var physicalCondition: Int = 0,
    @ColumnInfo(name = "glassSelection")
    var glassSelection: Int = 0,
    @ColumnInfo(name = "isNotificationActive")
    var isNotificationActive: Boolean = false
)