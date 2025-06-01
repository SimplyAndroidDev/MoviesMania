package com.coder.moviesmania.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.coder.moviesmania.R
import com.coder.moviesmania.databinding.ActivityMovieDetailsBinding
import com.coder.moviesmania.ui.viewmodel.MovieDetailsState
import com.coder.moviesmania.ui.viewmodel.MovieDetailsViewModel
import com.coder.moviesmania.util.setChipsOrGone
import com.coder.moviesmania.util.setTextOrGone
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Locale

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var viewModel: MovieDetailsViewModel
    private var isSaveButtonClicked = false
    private var movieId: Long = -1L
    private var source: String = SOURCE_UNKNOWN
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Setup edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(0,0,0, systemBars.bottom)
            insets
        }
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MovieDetailsViewModel::class.java]

        // Get movie ID from intent
        movieId = intent.getLongExtra(EXTRA_MOVIE_ID, -1L)
        if (movieId == -1L) {
            showError(getString(R.string.invalid_movie_id))
            return
        }

        // Get source from intent
        source = intent.getStringExtra(EXTRA_SOURCE) ?: SOURCE_UNKNOWN
        
        setupClickListeners()
        observeViewModel()
        
        // Load movie details
        loadMovieDetails()
    }
    
    private fun setupClickListeners() {
        binding.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        binding.saveButton.setOnClickListener {
            viewModel.currentMovie?.let {
                isSaveButtonClicked = true
                viewModel.toggleSaveMovie(it)
            }
        }
        
        binding.noDataView.noDataRetryButton.setOnClickListener {
            loadMovieDetails()
        }
    }
    
    private fun loadMovieDetails() {
        viewModel.getMovieDetails(movieId, source)
    }
    
    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.movieDetails.observe(this) { state ->
            when (state) {
                is MovieDetailsState.Loading -> {
                    showLoading()
                }
                is MovieDetailsState.Success -> {
                    showContent()
                    populateUI(state.data)
                }
                is MovieDetailsState.Error -> {
                    showError(getString(state.messageResId))
                }
            }
        }
        
        // Observe saved status
        viewModel.isSaved.observe(this) { isSaved ->
            updateSaveButton(isSaved)
        }
    }
    
    private fun populateUI(movie: com.coder.moviesmania.data.model.MovieDetails) {
        // Set title and tagline
        binding.movieTitleTextView.setTextOrGone(movie.title)
        binding.taglineTextView.setTextOrGone(movie.tagline)
        
        // Set overview
        binding.overviewTextView.setTextOrGone(movie.overview)
        binding.overviewLabelTextView.visibility = 
            if (movie.overview.isNullOrEmpty()) View.GONE else View.VISIBLE
        
        // Set rating and votes
        val ratingText = if (movie.rating != null && movie.rating > 0) {
            getString(R.string.rating_format, movie.rating, movie.voteCount)
        } else {
            null
        }
        binding.ratingTextView.setTextOrGone(ratingText)
        
        // Set release year
        val year = try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = movie.releaseDate?.let { dateFormat.parse(it) }
            date?.let {
                SimpleDateFormat("yyyy", Locale.getDefault()).format(it)
            }
        } catch (_: Exception) {
            null
        }
        binding.releaseYearTextView.setTextOrGone(year)
        
        // Set runtime
        val runtimeText = if (movie.runtime > 0) getString(R.string.minutes_format, movie.runtime) else null
        binding.runtimeTextView.setTextOrGone(runtimeText)
        
        // Set production companies
        val companyNames = movie.productionCompanies?.joinToString(", ") { 
            it.name ?: "Unknown"
        }
        binding.productionTextView.setTextOrGone(companyNames)
        binding.productionLabelTextView.visibility = 
            if (companyNames.isNullOrEmpty()) View.GONE else View.VISIBLE
        
        // Add genre chips
        binding.genresChipGroup.setChipsOrGone(movie.genres)
        movie.genres?.forEach { genre ->
            val chip = Chip(this)
            chip.text = genre.name
            binding.genresChipGroup.addView(chip)
        }

        binding.originalTitleTextView.setTextOrGone(movie.originalTitle)
        binding.originalTitleLabelTextView.visibility =
            if (movie.originalTitle.isNullOrEmpty()) View.GONE else View.VISIBLE
        
        // Set status
        binding.statusTextView.setTextOrGone(movie.status)
        binding.statusLabelTextView.visibility = 
            if (movie.status.isEmpty()) View.GONE else View.VISIBLE
        
        // Set languages
        val languages = movie.spokenLanguages?.joinToString(", ") { 
            it.englishName ?: it.name ?: it.iso ?: ""
        }
        binding.languagesTextView.setTextOrGone(languages)
        binding.languagesLabelTextView.visibility = 
            if (languages.isNullOrEmpty()) View.GONE else View.VISIBLE
        
        // Load backdrop image with higher quality
        if (!movie.backdropPath.isNullOrEmpty()) {
            val backdropUrl = "https://image.tmdb.org/t/p/original${movie.backdropPath}"
            Glide.with(this)
                .load(backdropUrl)
                .centerCrop()
                .override(1920, 1080)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_launcher_background)
                .into(binding.backdropIv)
        }

        // Load poster image
        if (!movie.posterPath.isNullOrEmpty()) {
            val logoUrl = "https://image.tmdb.org/t/p/w342${movie.posterPath}"
            Glide.with(this)
                .load(logoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_launcher_background)
                .into(binding.posterPathIv)
            binding.posterPathIv.visibility = View.VISIBLE
        } else {
            binding.posterPathIv.visibility = View.GONE
        }
    }
    
    private fun showLoading() {
        // Show loading indicator
        binding.progressBar.visibility = View.VISIBLE
        
        // Hide error view
        binding.noDataView.root.visibility = View.GONE
        
        // Hide content views
        binding.appBarLayout.visibility = View.GONE
        binding.saveButton.visibility = View.GONE
        binding.backdropIv.visibility = View.GONE
        binding.contentScrollView.visibility = View.GONE
    }
    
    private fun showContent() {
        // Hide loading and error indicators
        binding.progressBar.visibility = View.GONE
        binding.noDataView.root.visibility = View.GONE
        
        // Show content views
        binding.appBarLayout.visibility = View.VISIBLE
        binding.saveButton.visibility = View.VISIBLE
        binding.backdropIv.visibility = View.VISIBLE
        binding.contentScrollView.visibility = View.VISIBLE
    }
    
    private fun showError(message: String) {
        // Hide loading indicator
        binding.progressBar.visibility = View.GONE
        
        // Show error view with message
        binding.noDataView.root.visibility = View.VISIBLE
        binding.noDataView.noDataTextView.text = message
        
        // Hide content views
        binding.appBarLayout.visibility = View.GONE
        binding.saveButton.visibility = View.GONE
        binding.backdropIv.visibility = View.GONE
        binding.contentScrollView.visibility = View.GONE
    }

    private fun updateSaveButton(isSaved: Boolean) {
        if (isSaved) {
            binding.saveButton.setImageResource(R.drawable.star_filled)
            if (isSaveButtonClicked) {
                Toast.makeText(this, getString(R.string.movie_saved), Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.saveButton.setImageResource(R.drawable.star_unfilled)
            // Only show the removed message if we're not just initializing the button
            if (isSaveButtonClicked) {
                Toast.makeText(this, getString(R.string.movie_removed), Toast.LENGTH_SHORT).show()
            }
        }

        isSaveButtonClicked = false
    }

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
        const val EXTRA_SOURCE = "extra_source"
        
        const val SOURCE_POPULAR = "source_popular"
        const val SOURCE_NOW_PLAYING = "source_now_playing"
        const val SOURCE_SAVED = "source_saved"
        const val SOURCE_SEARCH = "source_search"
        const val SOURCE_UNKNOWN = "source_unknown"
    }
} 