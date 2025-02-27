package com.adityacode.grownest.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adityacode.grownest.R
import com.adityacode.grownest.data.GardeningTips
import com.adityacode.grownest.data.Plant
import com.adityacode.grownest.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.app.ActivityOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PlantViewModel by viewModels()
    private lateinit var adapter: PlantAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var plantList: androidx.recyclerview.widget.RecyclerView
    private lateinit var fabAddPlant: FloatingActionButton
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupNavigation()
        setupObservers()
        setupTipOfTheDay()

        toolbar = binding.toolbar
        plantList = binding.plantList
        fabAddPlant = binding.fabAddPlant
        bottomNavigation = binding.bottomNavigation

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupRecyclerView() {
        adapter = PlantAdapter { plant ->
            showDeleteConfirmationDialog(plant)
        }
        
        binding.plantList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(false)
            
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.bottom = resources.getDimensionPixelSize(R.dimen.list_item_spacing)
                    }
                })
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshPlants()
        }
    }

    private fun setupNavigation() {
        binding.fabAddPlant.setOnClickListener {
            navigateToAddPlant()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_add_plant -> {
                    navigateToAddPlant()
                    false
                }
                R.id.nav_community -> {
                    startActivity(Intent(this, CommunityActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun setupObservers() {
        viewModel.plants.observe(this) { plants ->
            Log.d("MainActivity", "Plants received: ${plants.size}")
            adapter.submitList(plants.toMutableList())
            
            binding.apply {
                if (plants.isEmpty()) {
                    plantList.animate()
                        .alpha(0f)
                        .withEndAction {
                            plantList.visibility = View.GONE
                            emptyView.visibility = View.VISIBLE
                            emptyView.alpha = 0f
                            emptyView.animate().alpha(1f)
                        }
                } else {
                    emptyView.visibility = View.GONE
                    plantList.visibility = View.VISIBLE
                    plantList.alpha = 1f
                    
                    plantList.post {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is OperationStatus.Success -> {
                    showToast(status.message)
                }
                is OperationStatus.Error -> {
                    showToast("Error: ${status.message}")
                }
            }
        }

        viewModel.isRefreshing.observe(this) { isRefreshing ->
            binding.apply {
                if (isRefreshing) {
                    shimmerLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmer()
                    plantList.visibility = View.GONE
                } else {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    plantList.visibility = View.VISIBLE
                }
                swipeRefresh.isRefreshing = isRefreshing
            }
        }
    }

    private fun setupTipOfTheDay() {
        val tip = GardeningTips.getRandomTip()
        binding.tipTitle.text = tip.title
        binding.tipDescription.text = tip.description
    }

    private fun showDeleteConfirmationDialog(plant: Plant) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_plant)
            .setMessage(getString(R.string.delete_plant_confirmation, plant.name))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deletePlant(plant)
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAddPlant() {
        val intent = Intent(this, AddPlantActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            binding.fabAddPlant,
            "add_plant_transition"
        )
        startActivity(intent, options.toBundle())
    }
}