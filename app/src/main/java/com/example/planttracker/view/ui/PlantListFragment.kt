package com.example.planttracker.view.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planttracker.R
import com.example.planttracker.view.adapter.PlantListAdapter
import com.example.planttracker.viewmodel.PlantViewModel
import com.example.planttracker.viewmodel.PlantViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception

class PlantListFragment : Fragment(), PlantListAdapter.OnCardClickListener {

    private lateinit var sharedViewModel: PlantViewModel

    private lateinit var mView: View

    private lateinit var plantListAdapter: PlantListAdapter

    private val newPlantActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = activity?.run {
            ViewModelProvider(this, PlantViewModelFactory(this.application)).get(PlantViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        plantListAdapter = PlantListAdapter(this.context!!, this)

        sharedViewModel.allPlants.observe(this, Observer { plants ->
            // Update the cached copy of plants in the adapter
            plants?.let { plantListAdapter?.setPlants(it) }})

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_plant_list, container, false)

        mView.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val intent = Intent(this.context, NewPlantActivity::class.java)
            startActivityForResult(intent, newPlantActivityRequestCode)
        }

        mView.findViewById<RecyclerView>(R.id.plant_list).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
            adapter = plantListAdapter
        }
        return mView
    }

    override fun onCardClick(id: Int) {
        sharedViewModel.selectPlant(id)

        val detailFragment = PlantDetailFragment()
        val transaction = fragmentManager?.beginTransaction()?.apply {
            replace(R.id.fragment_container, detailFragment)
            addToBackStack(null)
        }

        transaction?.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newPlantActivityRequestCode && resultCode == Activity.RESULT_OK) {
            createPlant(data)
        }
    }

    private fun createPlant(data: Intent?){
        val extras = data!!.extras!!
        sharedViewModel.addPlant(
            extras.getString("Nickname"),
            extras.getString("Fullname"),
            extras.getString("photoPath"),
            extras.getString("LastWater"),
            extras.getInt("NextWater"),
            extras.getString("Sunlight"),
            extras.getString("Water"),
            extras.getString("Soil"),
            extras.getString("Warning"))
    }


}