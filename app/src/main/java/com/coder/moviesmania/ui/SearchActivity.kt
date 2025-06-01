package com.coder.moviesmania.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.coder.moviesmania.R
import com.coder.moviesmania.data.model.MoviesApiResponseState
import com.coder.moviesmania.databinding.ActivitySearchBinding
import com.coder.moviesmania.ui.adapter.MoviesAdapter
import com.coder.moviesmania.ui.viewmodel.MoviesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: MoviesViewModel
    private lateinit var searchAdapter: MoviesAdapter
    
    private var searchJob: Job? = null
    private val searchDelayMillis = 500L // Debounce delay in milliseconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MoviesViewModel::class]
        
        setupUI()
        setupRecyclerView()
        setupSearchFunctionality()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }
        
        // Clear button
        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
        }
        
        // No data view setup
        binding.noDataView.noDataTextView.text = getString(R.string.no_search_results)
        binding.noDataView.noDataRetryButton.visibility = View.GONE
    }
    
    private fun setupRecyclerView() {
        searchAdapter = MoviesAdapter()
        binding.searchResultsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = searchAdapter
            setHasFixedSize(true)
        }
        
        searchAdapter.setOnItemClickListener { movie ->
            // Navigate to movie details screen
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, movie.id)
                putExtra(MovieDetailsActivity.EXTRA_SOURCE, MovieDetailsActivity.SOURCE_SEARCH)
            }
            startActivity(intent)
        }
    }
    
    private fun setupSearchFunctionality() {
        // Handle keyboard search action
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.searchEditText.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
        
        // Handle text changes with debouncing
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Show/hide clear button based on text
                binding.clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    debouncedSearch(query)
                } else {
                    // Clear results when search box is empty
                    showEmptyState()
                }
            }
        })
    }
    
    private fun debouncedSearch(query: String) {
        // Cancel any existing search job
        searchJob?.cancel()
        
        // Create a new coroutine to delay the search
        searchJob = CoroutineScope(Dispatchers.IO).launch {
            delay(searchDelayMillis)
            withContext(Dispatchers.Main) {
                performSearch(query)
            }
        }
    }
    
    private fun performSearch(query: String) {
        if (query.isBlank()) return
        
        // Show loading state
        showLoading()
        
        // Perform search
        viewModel.searchMovies(query)
    }
    
    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { state ->
            when (state) {
                is MoviesApiResponseState.Loading -> {
                    showLoading()
                }
                is MoviesApiResponseState.Success -> {
                    val movies = state.data.movies
                    if (movies.isNotEmpty()) {
                        showResults()
                        searchAdapter.submitList(movies)
                    } else {
                        showEmptyState()
                    }
                }
                is MoviesApiResponseState.Error -> {
                    showError(state.message)
                }
            }
        }
    }
    
    private fun showLoading() {
        binding.searchProgressBar.visibility = View.VISIBLE
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.noDataView.root.visibility = View.GONE
    }
    
    private fun showResults() {
        binding.searchProgressBar.visibility = View.GONE
        binding.searchResultsRecyclerView.visibility = View.VISIBLE
        binding.noDataView.root.visibility = View.GONE
    }
    
    private fun showEmptyState() {
        binding.searchProgressBar.visibility = View.GONE
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.noDataView.root.visibility = View.VISIBLE
    }
    
    private fun showError(errorMessage: String) {
        binding.searchProgressBar.visibility = View.GONE
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.noDataView.root.visibility = View.VISIBLE
        binding.noDataView.noDataTextView.text = errorMessage
    }
} 