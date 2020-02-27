package com.example.planttracker.model.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlantDao {
    @Query("SELECT * FROM plant ORDER BY days_to_next_water ASC")
    fun getAll(): LiveData<List<Plant>>

    @Query("SELECT COUNT(plant_nickname) FROM plant")
    fun getRowCount(): Int

    @Query("SELECT * FROM plant WHERE plant_nickname LIKE :name or plant_fullname LIKE :name")
    fun searchByName(name: String): LiveData<List<Plant>>

    @Query("SELECT * FROM plant WHERE id = :id LIMIT 1")
    fun searchByID(id: Int): Plant

    @Query("SELECT COUNT(1) FROM plant WHERE days_to_next_water = 0")
    fun searchPlantsNeedWatering(): Int

    @Query("UPDATE plant SET days_to_next_water=:days WHERE id= :id")
    fun updatePlantNextWater(id: Int, days: Int)

    @Query("UPDATE plant SET last_watered= :date WHERE id = :id")
    fun updatePlantLastWater(id: Int, date: String)

    @Query("UPDATE plant SET plant_nickname= :nickname, plant_fullname= :fullName, photo_filepath = :photoFilepath, last_watered = :lastWatered, days_to_next_water = :nextWater, sunlight_req = :sunReq, water_req = :waterReq, soil_req = :soilReq, warnings = :warnings WHERE id= :id")
    fun updatePlant(id: Int, nickname: String?, fullName: String?, photoFilepath: String?, lastWatered: String?, nextWater: Int?, sunReq: String?,
                    waterReq: String?, soilReq: String?, warnings: String?)

    @Query("UPDATE plant SET days_to_next_water = days_to_next_water - 1 WHERE days_to_next_water > 0")
    fun decreaseDaysToWater()

    @Insert
    suspend fun insert(plant: Plant)

    @Delete
    suspend fun delete(plant: Plant)
}