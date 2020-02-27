package com.example.planttracker.model.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Singleton database class.
 * To update the db schema, must increment the version number and add a migration.
 */

@Database(entities = [Plant::class], version = 4, exportSchema = false)
abstract class PlantDatabase: RoomDatabase() {

    abstract fun plantDao(): PlantDao

    companion object {

        // Database is a Singleton- only has 1 instance
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        // Added column for 'full name' of plant
        private val MIGRATION_2_3 = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Plant ADD COLUMN plant_fullname TEXT")
            }
        }


        fun getInstance(context: Context): PlantDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, PlantDatabase::class.java, "plant_database")
                        .addMigrations(MIGRATION_2_3)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE as PlantDatabase
        }
    }
}