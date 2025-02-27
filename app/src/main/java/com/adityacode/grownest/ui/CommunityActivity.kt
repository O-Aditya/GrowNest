package com.adityacode.grownest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityacode.grownest.R
import com.adityacode.grownest.data.CommunityPost
import com.adityacode.grownest.databinding.ActivityCommunityBinding
import com.adityacode.grownest.databinding.DialogAddPostBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.TimeUnit

class CommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityBinding
    private lateinit var adapter: CommunityPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener { 
                finish() // or use onBackPressedDispatcher.onBackPressed()
            }
            title = getString(R.string.community)
        }
    }

    private fun setupRecyclerView() {
        adapter = CommunityPostAdapter()
        binding.postsList.apply {
            layoutManager = LinearLayoutManager(this@CommunityActivity)
            adapter = this@CommunityActivity.adapter
        }
        
        // Add sample data
        adapter.submitList(getSamplePosts())
    }

    private fun setupFab() {
        binding.fabAddPost.setOnClickListener {
            showAddPostDialog()
        }
    }

    private fun showAddPostDialog() {
        val dialogBinding = DialogAddPostBinding.inflate(layoutInflater)
        
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.new_post)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.post) { _, _ ->
                // Handle post creation
                val title = dialogBinding.editTitle.text.toString()
                val content = dialogBinding.editContent.text.toString()
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    // Add post to the list
                    val newPost = CommunityPost(
                        id = System.currentTimeMillis(),
                        title = title,
                        content = content,
                        authorName = "You",
                        timestamp = System.currentTimeMillis()
                    )
                    val currentList = adapter.currentList.toMutableList()
                    currentList.add(0, newPost)
                    adapter.submitList(currentList)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun getSamplePosts(): List<CommunityPost> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            CommunityPost(
                id = 1,
                title = "Tips for Growing Tomatoes",
                content = "1. Choose a sunny location\n2. Use well-draining soil\n3. Water consistently\n4. Add support early\n5. Prune regularly",
                authorName = "GardenGuru",
                timestamp = currentTime - TimeUnit.DAYS.toMillis(1)
            ),
            CommunityPost(
                id = 2,
                title = "Best Indoor Plants for Beginners",
                content = "Snake Plant, Pothos, and ZZ Plant are excellent choices for beginners. They're hardy and can tolerate various light conditions.",
                authorName = "PlantLover",
                timestamp = currentTime - TimeUnit.HOURS.toMillis(5)
            ),
            CommunityPost(
                id = 3,
                title = "Natural Pest Control Methods",
                content = "Try using neem oil, companion planting, or introducing beneficial insects to control pests naturally.",
                authorName = "OrganicGardener",
                timestamp = currentTime - TimeUnit.MINUTES.toMillis(30)
            )
        )
    }
}