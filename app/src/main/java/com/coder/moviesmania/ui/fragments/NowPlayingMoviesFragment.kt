package com.coder.moviesmania.ui.fragments

import androidx.lifecycle.ViewModelProvider
import com.coder.moviesmania.ui.MovieDetailsActivity
import com.coder.moviesmania.ui.viewmodel.MoviesViewModel

class NowPlayingMoviesFragment : BaseMoviesFragment() {
    
    override fun initViewModel() {
        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
    }
    
    override fun observeViewModel() {
        viewModel.nowPlayingMoviesResponse.observe(viewLifecycleOwner) { response ->
            handleMoviesResponse(response)
        }
    }
    
    override fun loadMovies() {
        viewModel.fetchNowPlayingMovies()
    }
    
    override fun getMovieSource(): String = MovieDetailsActivity.SOURCE_NOW_PLAYING
    
    companion object {
        fun newInstance() = NowPlayingMoviesFragment()
    }
} 