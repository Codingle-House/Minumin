package id.co.minumin.domain.repository

import id.co.minumin.const.MinuminConstant.Time.HOURS
import id.co.minumin.const.MinuminConstant.Time.MILLIS
import id.co.minumin.const.MinuminConstant.Time.MINUTES
import id.co.minumin.const.MinuminConstant.Time.SECONDS
import id.co.minumin.data.dto.BackupUserSettingDto
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.data.mapper.DataMapper
import id.co.minumin.domain.datasource.LocalDataSource
import id.co.minumin.util.DateTimeUtil
import id.co.minumin.util.DateTimeUtil.SEVEN_DAYS
import java.util.*
import javax.inject.Inject

/**
 * Created by pertadima on 11,February,2021
 */

class AppRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val dataMapper: DataMapper
) {
    suspend fun doDrinkWaterAndGet(drinkDto: DrinkDto): List<DrinkDto> {
        return localDataSource.doDrinkWaterAndGet(dataMapper.convertDrinkDtoToEntity(drinkDto))
            .filter { !it.isDeleted }
            .map { dataMapper.convertDrinkEntityToDto(it) }
    }

    suspend fun getDrinkWater(date: Date) = localDataSource.getDrinkWater(date)
        .filter { !it.isDeleted }
        .map { dataMapper.convertDrinkEntityToDto(it) }

    suspend fun doEditDrinkWater(drinkDto: DrinkDto) =
        localDataSource.doEditDrinkWater(dataMapper.convertDrinkDtoToEntity(drinkDto))

    suspend fun getDrinkWaterBetweenDate(startDate: Date, endDate: Date): List<DrinkDto> {
        val millis = endDate.time - startDate.time
        val daysDifference = (millis / (MILLIS * SECONDS * MINUTES * HOURS))
        val list = when (daysDifference.toInt()) {
            SEVEN_DAYS -> localDataSource.getDrinkWaterLast7Days()
            else -> localDataSource.getDrinkWaterLast30Days()
        }
        return list.filter { !it.isDeleted }.map { dataMapper.convertDrinkEntityToDto(it) }
    }

    suspend fun doBackup(backupUserSettingDto: BackupUserSettingDto) =
        localDataSource.doBackup(dataMapper.convertBackupUserSettingEntity(backupUserSettingDto))

    suspend fun getBackupData() = localDataSource.getBackupData().map {
        dataMapper.convertBackupUserSettingEntityToDto(it)
    }
}