package com.coder.moviesmania.ui.fragments

import androidx.lifecycle.ViewModelProvider
import com.coder.moviesmania.ui.MovieDetailsActivity
import com.coder.moviesmania.ui.viewmodel.MoviesViewModel

class PopularMoviesFragment : BaseMoviesFragment() {
    
    override fun initViewModel() {
        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
    }
    
    override fun observeViewModel() {
        viewModel.popularMoviesResponse.observe(viewLifecycleOwner) { response ->
            handleMoviesResponse(response)
        }
    }
    
    override fun loadMovies() {
        viewModel.fetchPopularMovies()
    }
    
    override fun getMovieSource(): String = MovieDetailsActivity.SOURCE_POPULAR
    
    companion object {
        fun newInstance() = PopularMoviesFragment()
    }
} 