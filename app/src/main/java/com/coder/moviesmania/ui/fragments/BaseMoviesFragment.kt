package com.coder.moviesmania.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.coder.moviesmania.R
import com.coder.moviesmania.data.model.MoviesApiResponseState
import com.coder.moviesmania.databinding.FragmentMoviesBinding
import com.coder.moviesmania.ui.MovieDetailsActivity
import com.coder.moviesmania.ui.adapter.MoviesAdapter
import com.coder.moviesmania.ui.viewmodel.MoviesViewModel

abstract class BaseMoviesFragment : Fragment() {
    
    private var _binding: FragmentMoviesBinding? = null
    protected val binding get() = _binding!!
    
    protected lateinit var viewModel: MoviesViewModel
    protected lateinit var moviesAdapter: MoviesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupRetryButton()
        initViewModel()
        observeViewModel()
        loadMovies()
    }
    
    private fun setupRecyclerView() {
        moviesAdapter = MoviesAdapter()
        binding.moviesRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = moviesAdapter
            setHasFixedSize(true)
        }
        
        moviesAdapter.setOnItemClickListener { movie ->
            // Navigate to movie details screen
            val intent = Intent(requireActivity(), MovieDetailsActivity::class.java).apply {
                putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, movie.id)
                putExtra(MovieDetailsActivity.EXTRA_SOURCE, getMovieSource())
            }

            startActivity(intent)
        }
    }
    
    private fun setupRetryButton() {
        binding.noDataView.noDataRetryButton.setOnClickListener {
            loadMovies()
        }
    }
    
    /**
     * Get the source of the movie list for this fragment
     */
    protected abstract fun getMovieSource(): String
    
    protected open fun handleMoviesResponse(response: MoviesApiResponseState) {
        when (response) {
            is MoviesApiResponseState.Loading -> {
                showLoading()
            }
            is MoviesApiResponseState.Success -> {
                val movies = response.data.movies
                if (movies.isNotEmpty()) {
                    showContent()
                    moviesAdapter.submitList(movies)
                } else {
                    showEmptyState()
                }
            }
            is MoviesApiResponseState.Error -> {
                showError(response.message)
            }
        }
    }
    
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.moviesRecyclerView.visibility = View.GONE
        binding.noDataView.root.visibility = View.GONE
    }
    
    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.noDataView.root.visibility = View.GONE
        binding.moviesRecyclerView.visibility = View.VISIBLE
    }
    
    private fun showEmptyState() {
        binding.progressBar.visibility = View.GONE
        binding.moviesRecyclerView.visibility = View.GONE
        
        binding.noDataView.root.visibility = View.VISIBLE
        binding.noDataView.noDataTextView.text = getString(R.string.no_data_available)
    }
    
    private fun showError(errorMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.moviesRecyclerView.visibility = View.GONE
        
        binding.noDataView.root.visibility = View.VISIBLE
        binding.noDataView.noDataTextView.text = errorMessage
    }
    
    abstract fun initViewModel()
    abstract fun observeViewModel()
    abstract fun loadMovies()
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 