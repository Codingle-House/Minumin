package id.co.minumin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.co.minumin.data.local.entity.BackupUserSettingEntity

/**
 * Created by pertadima on 16,February,2021
 */

@Dao
interface BackupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun doBackup(data: BackupUserSettingEntity)

    @Query("SELECT * FROM tbl_backup")
    suspend fun getBackupData(): List<BackupUserSettingEntity>

    @Query("DELETE FROM tbl_backup")
    fun deleteAllData()
}