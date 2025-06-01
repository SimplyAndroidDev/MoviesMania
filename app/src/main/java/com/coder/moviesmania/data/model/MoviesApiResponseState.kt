package com.coder.moviesmania.data.model

/**
 * A sealed class that encapsulates the result of a network request
 * Used to handle success, loading, and error states in a consistent way
 */
sealed class MoviesApiResponseState {
    data class Success(val data: MovieResponse) : MoviesApiResponseState()
    data class Error(val message: String, val exception: Exception? = null) : MoviesApiResponseState()
    object Loading : MoviesApiResponseState()
} 