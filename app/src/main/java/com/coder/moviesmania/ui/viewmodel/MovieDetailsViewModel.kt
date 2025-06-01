package com.coder.moviesmania.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coder.moviesmania.R
import com.coder.moviesmania.data.model.MovieDetails
import com.coder.moviesmania.data.network.RetrofitClient
import com.coder.moviesmania.data.repository.MovieRepository
import com.coder.moviesmania.ui.MovieDetailsActivity
import com.coder.moviesmania.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsViewModel : ViewModel() {
    
    private val repository = MovieRepository(RetrofitClient.createApiService())
    
    // LiveData for movie details response state
    private val _movieDetails = MutableLiveData<MovieDetailsState>()
    val movieDetails: LiveData<MovieDetailsState> = _movieDetails
    
    // LiveData to track if a movie is saved
    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved
    
    // Store the current movie
    var currentMovie: MovieDetails? = null
        private set
    
    /**
     * Fetch movie details by ID and update the appropriate database based on source
     * @param movieId The ID of the movie to fetch
     * @param source The source from which the movie details screen was opened
     */
    fun getMovieDetails(movieId: Long, source: String = MovieDetailsActivity.SOURCE_UNKNOWN) {
        _movieDetails.value = MovieDetailsState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            // Check if this movie is saved, regardless of network state
            val saved = repository.isMovieSaved(movieId)
            withContext(Dispatchers.Main) {
                _isSaved.value = saved
            }
            
            // Check for network connectivity
            if (!NetworkUtils.isNetworkAvailable()) {
                // Try to get movie details from database
                val cachedDetails = repository.getMovieDetailsFromDatabase(movieId, source)
                
                withContext(Dispatchers.Main) {
                    if (cachedDetails != null) {
                        // We have cached details, use them
                        currentMovie = cachedDetails
                        _movieDetails.value = MovieDetailsState.Success(cachedDetails)
                    } else {
                        // No cached details, show error
                        _movieDetails.value = MovieDetailsState.Error(NetworkUtils.getNoInternetErrorMessage())
                    }
                }

                return@launch
            }
            
            // We have network connectivity, proceed with API call
            try {
                val response = repository.getMovieDetails(movieId)
                
                // Update movie details in both popular and now playing databases if the movie exists in them
                if (response != null) {
                    repository.updateMovieDetailsInDatabases(response)
                    
                    // Store the current movie
                    currentMovie = response
                }
                
                withContext(Dispatchers.Main) {
                    if (response != null) {
                        _movieDetails.value = MovieDetailsState.Success(response)
                    } else {
                        _movieDetails.value = MovieDetailsState.Error(R.string.error_server)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    _movieDetails.value = MovieDetailsState.Error(R.string.error_unknown)
                }
            }
        }
    }
    
    /**
     * Toggle saved status of a movie
     */
    fun toggleSaveMovie(movie: MovieDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            val isCurrentlySaved = repository.isMovieSaved(movie.id)
            
            if (isCurrentlySaved) {
                repository.removeSavedMovie(movie.id)
            } else {
                repository.saveMovieDetails(movie)
            }

            val isRemoved = repository.isMovieSaved(movie.id)
            withContext(Dispatchers.Main) {
                _isSaved.value = isRemoved
            }
        }
    }
}

// Sealed class for movie details state
sealed class MovieDetailsState {
    object Loading : MovieDetailsState()
    data class Success(val data: MovieDetails) : MovieDetailsState()
    data class Error(val messageResId: Int) : MovieDetailsState()
} 