package com.example.planttracker.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlantViewModelFactory(application: Application): ViewModelProvider.Factory {

    private val mApplication = application

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlantViewModel(mApplication) as T
    }
}