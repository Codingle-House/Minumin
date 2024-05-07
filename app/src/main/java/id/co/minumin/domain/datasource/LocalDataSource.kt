package id.co.minumin.domain.datasource

import id.co.minumin.data.local.AppDatabase
import id.co.minumin.data.local.entity.BackupUserSettingEntity
import id.co.minumin.data.local.entity.DrinkEntity
import java.util.*
import javax.inject.Inject

/**
 * Created by pertadima on 11,February,2021
 */

class LocalDataSource @Inject constructor(private val appDatabase: AppDatabase) {
    suspend fun doDrinkWater(drinkEntity: DrinkEntity) =
        appDatabase.drinkDao().doDrinkWater(drinkEntity)

    suspend fun doDrinkWaterAndGet(drinkEntity: DrinkEntity): List<DrinkEntity> {
        appDatabase.drinkDao().doDrinkWater(drinkEntity)
        return appDatabase.drinkDao().getDrinkWater(drinkEntity.date ?: Date())
    }

    suspend fun getDrinkWater(date: Date): List<DrinkEntity> {
        return appDatabase.drinkDao().getDrinkWater(date)
    }

    suspend fun doEditDrinkWater(drinkEntity: DrinkEntity) =
        appDatabase.drinkDao().doEditDrinkWater(drinkEntity)

    suspend fun getDrinkWaterLast7Days() =
        appDatabase.drinkDao().getDrinkWaterLast7Days()

    suspend fun getDrinkWaterLast30Days() =
        appDatabase.drinkDao().getDrinkWaterLast30Days()

    suspend fun doBackup(backupUserSettingEntity: BackupUserSettingEntity) {
        appDatabase.backUpDao().doBackup(backupUserSettingEntity)
    }

    suspend fun getBackupData() = appDatabase.backUpDao().getBackupData()
}