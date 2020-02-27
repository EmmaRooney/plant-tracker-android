package com.example.planttracker.viewmodel

import android.app.Application
import android.os.Debug
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.planttracker.model.data.Plant
import com.example.planttracker.model.data.PlantDatabase
import com.example.planttracker.model.repository.PlantRepository
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.time.LocalDate
import java.util.*

/**
 * Contains data-handling logic for UI to communicate with models
 *
 * @param: the application context
 */
class PlantViewModel(application: Application): AndroidViewModel(application) {

    private val repository: PlantRepository
    val allPlants: LiveData<List<Plant>>

    var selectedPlant: Plant?

    init {
        val plantsDao = PlantDatabase.getInstance(application).plantDao()
        repository = PlantRepository(plantsDao)
        allPlants = repository.allPlants

        selectedPlant = null
    }


    fun addPlant(name: String?, fullname: String?, photoFilepath: String?, lastWatered: String?, nextWater: Int?, sunReq: String?,
                 waterReq: String?, soilReq: String?, warnings: String?) {
        val plant = Plant(name, fullname, photoFilepath, lastWatered, nextWater, sunReq, waterReq, soilReq, warnings)
        insert(plant)
    }

    /**
     * Wrapper method for PlantRepository's insert()
     * @param plant: Plant object to insert into the PlantModel DAO
     */
    private fun insert(plant: Plant) = viewModelScope.launch {
        repository.insert(plant)
    }

    fun delete(plant: Plant) = viewModelScope.launch {
        repository.delete(plant)
        selectedPlant = null
    }

    fun selectPlant(id: Int) {
        val plant = repository.getPlant(id)
        selectedPlant = plant
    }

    fun updatePlant(id: Int, name: String?, fullname: String?, photoFilepath: String?, lastWatered: String?, nextWater: Int?, sunReq: String?,
                    waterReq: String?, soilReq: String?, warnings: String?) {
        repository.updatePlant(
            id,
            name,
            fullname,
            photoFilepath,
            lastWatered,
            nextWater,
            sunReq,
            waterReq,soilReq,
            warnings)

        selectPlant(selectedPlant!!.id)
    }

    fun updateSelectedPlantNextWater(days: Int) {
        repository.updatePlantNextWater(selectedPlant!!.id, days)
        // refresh selected plant
        selectPlant(selectedPlant!!.id)
    }

    fun updateSelectedPlantLastWater() {

        repository.updatePlantLastWater(selectedPlant!!.id, getDateToday())
        // refresh selected plant
        selectPlant(selectedPlant!!.id)
    }

    fun updateSelectedPlantLastWater(date: String) {

        repository.updatePlantLastWater(selectedPlant!!.id, date)
        // refresh selected plant
        selectPlant(selectedPlant!!.id)
    }

    fun updateAllPlantsDaysToWater() {
        repository.decreaseDaysToWater()
    }

    private fun getDateToday(): String {
        val dateToday = Calendar.getInstance()
        val day = dateToday.get(Calendar.DAY_OF_MONTH).toString()
        val month = dateToday.get(Calendar.MONTH) + 1
        val year = dateToday.get(Calendar.YEAR)
        return "$day/$month/$year"
    }

}