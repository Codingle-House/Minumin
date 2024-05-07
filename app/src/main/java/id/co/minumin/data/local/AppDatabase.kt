package id.co.minumin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.co.minumin.data.local.dao.BackupDao
import id.co.minumin.data.local.dao.DrinkDao
import id.co.minumin.data.local.entity.BackupUserSettingEntity
import id.co.minumin.data.local.entity.DrinkEntity
import id.co.minumin.util.TimeStampConverter

/**
 * Created by pertadima on 11,February,2021
 */

@Database(
    entities = [
        DrinkEntity::class,
        BackupUserSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimeStampConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drinkDao(): DrinkDao
    abstract fun backUpDao(): BackupDao
}