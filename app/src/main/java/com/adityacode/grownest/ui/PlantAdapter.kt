package com.adityacode.grownest.ui

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.adityacode.grownest.R
import com.adityacode.grownest.data.Plant
import com.adityacode.grownest.databinding.ItemPlantBinding

class PlantAdapter(private val onDeleteClick: (Plant) -> Unit) : 
    ListAdapter<Plant, PlantAdapter.PlantViewHolder>(PlantDiffCallback()) {

    init {
        Log.d("PlantAdapter", "Adapter initialized")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        Log.d("PlantAdapter", "Creating new ViewHolder")
        val binding = ItemPlantBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = getItem(position)
        Log.d("PlantAdapter", "Binding plant at position $position: ${plant.name}")
        holder.bind(plant, onDeleteClick)
        
        // Make sure the item view is visible
        holder.itemView.visibility = View.VISIBLE
        holder.itemView.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    private class PlantDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem == newItem
        }
    }

    class PlantViewHolder(
        private val binding: ItemPlantBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(plant: Plant, onDeleteClick: (Plant) -> Unit) {
            binding.apply {
                Log.d("PlantAdapter", "Setting up plant: ${plant.name}")
                
                // Set texts
                plantName.text = plant.name
                plantSpecies.text = plant.species
                wateringChip.text = "${plant.wateringFrequency} days"

                // Make sure views are visible
                plantName.visibility = View.VISIBLE
                plantSpecies.visibility = View.VISIBLE
                wateringChip.visibility = View.VISIBLE
                root.visibility = View.VISIBLE

                btnDelete.setOnClickListener {
                    onDeleteClick(plant)
                }

                // Set a default image first
                plantImage.setImageResource(R.drawable.ic_plant_placeholder)

                // Then try to load image if URL exists
                plant.imageUrl?.let { url ->
                    Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.ic_plant_placeholder)
                        .error(R.drawable.ic_plant_placeholder)
                        .into(plantImage)
                }
            }
        }
    }
}