package com.example.planttracker.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.example.planttracker.R
import com.example.planttracker.model.data.Plant
import com.example.planttracker.util.ImageUtil
import java.lang.ref.WeakReference

/**
 * Connects the PlantModel data to the fragment_plant_list view
 *
 * @param context: app context
 */
class PlantListAdapter internal constructor(private val context: Context, private val listener: OnCardClickListener): RecyclerView.Adapter<PlantListAdapter.PlantViewHolder>() {

    // Cached copy of plants
    private var plants = emptyList<Plant>()

    /**
     * Represents each item in data, used to display the items
     */
    inner class PlantViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.plant_card)
        val plantNameView: TextView = view.findViewById(R.id.plant_nickname) as TextView
        val lastWaterView: TextView = view.findViewById(R.id.last_watered) as TextView
        val daysUntilWaterView: TextView = view.findViewById(R.id.days_until_water) as TextView
        val plantImageView: ImageView = view.findViewById(R.id.plant_photo) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        // Create new view object from fragment_plant_list_item xml layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_plant_list_item, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val currentPlant = plants[position]

        holder.plantNameView.text = currentPlant.plantNickname
        holder.lastWaterView.text = currentPlant.lastWatered
        holder.daysUntilWaterView.text = currentPlant.nextWater.toString()

        if (!currentPlant.photoFilepath.isNullOrBlank()) {
            ImageUtil.loadPhoto(currentPlant.photoFilepath, WeakReference( holder.plantImageView))
        }

        // Highlight if plant needs watering
        if (currentPlant.nextWater == 0) {
            holder.daysUntilWaterView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentLight))
        } else {
            holder.daysUntilWaterView.setTextColor(Color.GRAY)
            holder.card.setCardBackgroundColor(Color.WHITE)
        }

        holder.card.setOnClickListener {
            listener.onCardClick(currentPlant.id)
        }
    }

    override fun getItemCount() = plants.size

    internal fun setPlants(plant: List<Plant>) {
        this.plants = plant
        notifyDataSetChanged()
    }

    interface OnCardClickListener {
        fun onCardClick(id: Int)
    }

}
