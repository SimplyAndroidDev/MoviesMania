package com.coder.moviesmania.ui.fragments

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.coder.moviesmania.ui.MovieDetailsActivity
import com.coder.moviesmania.ui.viewmodel.MoviesViewModel

class SavedMoviesFragment : BaseMoviesFragment() {
    
    override fun initViewModel() {
        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
    }
    
    override fun observeViewModel() {
        viewModel.savedMovies.observe(viewLifecycleOwner) { movies ->
            binding.progressBar.visibility = View.GONE
            
            if (movies.isNotEmpty()) {
                binding.moviesRecyclerView.visibility = View.VISIBLE
                binding.noDataView.root.visibility = View.GONE
                moviesAdapter.submitList(movies)
            } else {
                binding.moviesRecyclerView.visibility = View.GONE
                binding.noDataView.root.visibility = View.VISIBLE
                binding.noDataView.noDataRetryButton.visibility = View.GONE
                binding.noDataView.noDataTextView.text = "No saved movies"
            }
        }
    }

    override fun loadMovies() {
    }
    
    override fun getMovieSource(): String = MovieDetailsActivity.SOURCE_SAVED
    
    companion object {
        fun newInstance() = SavedMoviesFragment()
    }
} 