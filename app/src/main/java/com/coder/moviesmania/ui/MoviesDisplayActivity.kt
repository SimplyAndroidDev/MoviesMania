package com.coder.moviesmania.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.coder.moviesmania.databinding.ActivityMoviesDisplayBinding
import com.coder.moviesmania.ui.adapter.MoviesPagerAdapter
import com.coder.moviesmania.ui.viewmodel.MoviesViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MoviesDisplayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoviesDisplayBinding
    private lateinit var viewModel: MoviesViewModel
    private lateinit var pagerAdapter: MoviesPagerAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize View Binding
        binding = ActivityMoviesDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MoviesViewModel::class]
        
        // Setup search icon click
        setupSearchIcon()
        
        // Setup ViewPager and Tabs
        setupViewPager()
    }
    
    private fun setupSearchIcon() {
        binding.searchIcon.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun setupViewPager() {
        pagerAdapter = MoviesPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false
        
        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = pagerAdapter.getTabTitle(position)
        }.attach()
    }
} 