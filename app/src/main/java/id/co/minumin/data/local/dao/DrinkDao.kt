package id.co.minumin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import id.co.minumin.data.local.entity.DrinkEntity
import java.util.*

/**
 * Created by pertadima on 11,February,2021
 */

@Dao
interface DrinkDao {
    @Insert(onConflict = REPLACE)
    suspend fun doDrinkWater(drinkEntity: DrinkEntity)

    @Query("SELECT * FROM tbl_drink WHERE date = :date")
    suspend fun getDrinkWater(date: Date): List<DrinkEntity>

    @Update(onConflict = REPLACE)
    suspend fun doEditDrinkWater(drinkEntity: DrinkEntity)

    @Query("SELECT * FROM tbl_drink WHERE date >=datetime('now', '-7 day')")
    suspend fun getDrinkWaterLast7Days(): List<DrinkEntity>

    @Query("SELECT * FROM tbl_drink WHERE date >=datetime('now', '-30 day')")
    suspend fun getDrinkWaterLast30Days(): List<DrinkEntity>
}