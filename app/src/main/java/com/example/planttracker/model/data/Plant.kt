package com.example.planttracker.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant")
data class Plant (
    @ColumnInfo(name = "plant_nickname") val plantNickname: String?,
    @ColumnInfo(name = "plant_fullname") val plantFullName: String?,
    @ColumnInfo(name = "photo_filepath") val photoFilepath: String?,
    @ColumnInfo(name = "last_watered") val lastWatered: String?,
    @ColumnInfo(name = "days_to_next_water") val nextWater: Int?,
    @ColumnInfo(name = "sunlight_req") val sunlightReq: String?,
    @ColumnInfo(name = "water_req") val waterReq: String?,
    @ColumnInfo(name = "soil_req") val soilReq: String?,
    @ColumnInfo(name = "warnings") val warnings: String?
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}