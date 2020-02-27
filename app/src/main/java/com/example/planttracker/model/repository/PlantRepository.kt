package com.example.planttracker.model.repository

import androidx.lifecycle.LiveData
import com.example.planttracker.model.data.Plant
import com.example.planttracker.model.data.PlantDao

/**
 * Provides a clean API for data access for the rest of the application (manages queries to multiple backends)
 *
 * @param plantDao: The DAO for the PlantModel table.
 */
class PlantRepository(private val plantDao: PlantDao) {

    val allPlants: LiveData<List<Plant>> = plantDao.getAll()

    fun getPlant(id: Int) : Plant {
        return plantDao.searchByID(id)
    }

    suspend fun insert(plant: Plant) {
        plantDao.insert(plant)
    }

    suspend fun delete(plant: Plant) {
        plantDao.delete(plant)
    }

    fun getTotalPlants() : Int {
        return plantDao.getRowCount()
    }

    fun updatePlantNextWater(id: Int, days: Int) {
        plantDao.updatePlantNextWater(id, days)
    }

    fun updatePlantLastWater(id: Int, date: String) {
        plantDao.updatePlantLastWater(id, date)
    }

    fun updatePlant(id: Int, nickname: String?, fullname: String?, photoFilepath: String?, lastWatered: String?, nextWater: Int?, sunReq: String?,
                    waterReq: String?, soilReq: String?, warnings: String?) {
        plantDao.updatePlant(id, nickname, fullname, photoFilepath, lastWatered, nextWater, sunReq, waterReq, soilReq, warnings)
    }

    fun decreaseDaysToWater() {
        plantDao.decreaseDaysToWater()
    }
}