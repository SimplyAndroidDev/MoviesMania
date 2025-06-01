package com.coder.moviesmania.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.coder.moviesmania.MoviesManiaApplication
import com.coder.moviesmania.R
import com.coder.moviesmania.data.model.MovieResponse
import com.coder.moviesmania.data.model.MoviesApiResponseState
import com.coder.moviesmania.data.network.RetrofitClient
import com.coder.moviesmania.data.repository.MovieRepository
import com.coder.moviesmania.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesViewModel : ViewModel() {

    private val repository = MovieRepository(RetrofitClient.createApiService())
    
    // LiveData for popular movies API response
    private val _popularMoviesResponse = MutableLiveData<MoviesApiResponseState>()
    val popularMoviesResponse: LiveData<MoviesApiResponseState> = _popularMoviesResponse
    
    // LiveData for now playing movies API response
    private val _nowPlayingMoviesResponse = MutableLiveData<MoviesApiResponseState>()
    val nowPlayingMoviesResponse: LiveData<MoviesApiResponseState> = _nowPlayingMoviesResponse
    
    val savedMovies = repository.getSavedMovies()
        .catch { emit(emptyList()) }
        .asLiveData()
    
    // LiveData for search results
    private val _searchResults = MutableLiveData<MoviesApiResponseState>()
    val searchResults: LiveData<MoviesApiResponseState> = _searchResults

    /**
     * Fetch popular movies from the repository and cache them
     */
    fun fetchPopularMovies(page: Int = 1) {
        _popularMoviesResponse.value = MoviesApiResponseState.Loading
        
        // Check for network connectivity
        if (!NetworkUtils.isNetworkAvailable()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Check if we have cached data
                    val hasCachedData = repository.hasPopularMoviesInCache()
                    if (hasCachedData) {
                        // Get data from cache
                        val cachedMovies = repository.getPopularMoviesFromCache().first()
                        val response = MovieResponse(
                            page = 1,
                            totalPages = 1,
                            totalResults = cachedMovies.size,
                            movies = cachedMovies
                        )

                        withContext(Dispatchers.Main) {
                            _popularMoviesResponse.value = MoviesApiResponseState.Success(response)
                        }
                    } else {
                        // No cached data, show error
                        withContext(Dispatchers.Main) {
                            _popularMoviesResponse.value = MoviesApiResponseState.Error(
                                getErrorMessageForResource(NetworkUtils.getNoInternetErrorMessage())
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // No cached data, show error
                    withContext(Dispatchers.Main) {
                        _popularMoviesResponse.value = MoviesApiResponseState.Error(
                            getErrorMessageForResource(NetworkUtils.getNoInternetErrorMessage())
                        )
                    }
                }
            }

            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getPopularMovies(page, true)
                withContext(Dispatchers.Main) {
                    response?.let {
                        _popularMoviesResponse.value = MoviesApiResponseState.Success(it)
                    } ?: run {
                        _popularMoviesResponse.value = MoviesApiResponseState.Error(
                            getErrorMessageForResource(R.string.error_server)
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    _popularMoviesResponse.value = MoviesApiResponseState.Error(
                        getErrorMessageForResource(R.string.error_unknown),
                        e
                    )
                }
            }
        }
    }
    
    /**
     * Fetch now playing movies from the repository and cache them
     */
    fun fetchNowPlayingMovies(page: Int = 1) {
        _nowPlayingMoviesResponse.value = MoviesApiResponseState.Loading
        
        // Check for network connectivity
        if (!NetworkUtils.isNetworkAvailable()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Check if we have cached data
                    val hasCachedData = repository.hasNowPlayingMoviesInCache()
                    if (hasCachedData) {
                        // Get data from cache
                        val cachedMovies = repository.getNowPlayingMoviesFromCache().first()
                        val response = MovieResponse(
                            page = 1,
                            totalPages = 1,
                            totalResults = cachedMovies.size,
                            movies = cachedMovies
                        )

                        withContext(Dispatchers.Main) {
                            _nowPlayingMoviesResponse.value = MoviesApiResponseState.Success(response)
                        }
                    } else {
                        // No cached data, show error
                        withContext(Dispatchers.Main) {
                            _nowPlayingMoviesResponse.value = MoviesApiResponseState.Error(
                                getErrorMessageForResource(NetworkUtils.getNoInternetErrorMessage())
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // No cached data, show error
                    withContext(Dispatchers.Main) {
                        _nowPlayingMoviesResponse.value = MoviesApiResponseState.Error(
                            getErrorMessageForResource(NetworkUtils.getNoInternetErrorMessage())
                        )
                    }
                }
            }
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getNowPlayingMovies(page, true)
                withContext(Dispatchers.Main) {
                    response?.let {
                        _nowPlayingMoviesResponse.value = MoviesApiResponseState.Success(it)
                    } ?: run {
                        _nowPlayingMoviesResponse.value = MoviesApiResponseState.Error(
                            getErrorMessageForResource(R.string.error_server)
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    _nowPlayingMoviesResponse.value = MoviesApiResponseState.Error(
                        getErrorMessageForResource(R.string.error_unknown),
                        e
                    )
                }
            }
        }
    }

    /**
     * Search for movies based on a query
     */
    fun searchMovies(query: String, page: Int = 1) {
        if (query.isBlank()) {
            return
        }
        
        _searchResults.value = MoviesApiResponseState.Loading
        
        // Check for network connectivity
        if (!NetworkUtils.isNetworkAvailable()) {
            _searchResults.value = MoviesApiResponseState.Error(
                getErrorMessageForResource(NetworkUtils.getNoInternetErrorMessage())
            )
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.searchMovies(query, page)
                withContext(Dispatchers.Main) {
                    response?.let {
                        _searchResults.value = MoviesApiResponseState.Success(it)
                    } ?: run {
                        _searchResults.value = MoviesApiResponseState.Error(
                            getErrorMessageForResource(R.string.error_server)
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    _searchResults.value = MoviesApiResponseState.Error(
                        getErrorMessageForResource(R.string.error_unknown),
                        e
                    )
                }
            }
        }
    }
    
    /**
     * Get error message string from resource
     */
    private fun getErrorMessageForResource(resourceId: Int): String {
        return MoviesManiaApplication.instance.getString(resourceId)
    }
} 