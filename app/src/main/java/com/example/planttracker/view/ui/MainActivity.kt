package com.example.planttracker.view.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.planttracker.R
import com.example.planttracker.util.NotificationUtil
import com.example.planttracker.util.WaterCheckerWorker

import kotlinx.android.synthetic.main.activity_main.*

import java.util.concurrent.TimeUnit.DAYS

class MainActivity : AppCompatActivity() {

    private val WORK_TAG = "WATER_PLANTS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        NotificationUtil.createNotificationChannel(this)

        // Background worker to check plant watering needs once a day
        val waterCheckerWorkerRequest = PeriodicWorkRequestBuilder<WaterCheckerWorker>(1, DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(WORK_TAG, ExistingPeriodicWorkPolicy.KEEP , waterCheckerWorkerRequest)

        if (this.fragment_container != null) {

            if (savedInstanceState != null) {
                return
            }

            val plantListFragment = PlantListFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, plantListFragment).commit()
        }
    }

    /**
     * Menu for changing order of plant list
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

}
