package id.co.minumin.data.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created by pertadima on 11,February,2021
 */

@Entity(tableName = "tbl_drink")
data class DrinkEntity(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "consumption")
    var consumption: Int = 0,
    @ColumnInfo(name = "time")
    var time: String = "",
    @ColumnInfo(name = "date")
    var date: Date? = null,
    @ColumnInfo(name = "isDeleted")
    var isDeleted: Boolean = false
)