package com.coder.moviesmania.data.repository

import com.coder.moviesmania.data.local.database.NowPlayingMoviesDatabase
import com.coder.moviesmania.data.local.database.PopularMoviesDatabase
import com.coder.moviesmania.data.local.database.SavedMoviesDatabase
import com.coder.moviesmania.data.model.Movie
import com.coder.moviesmania.data.model.MovieDetails
import com.coder.moviesmania.data.model.MovieResponse
import com.coder.moviesmania.data.network.api.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for handling movie-related operations
 * Acts as a single source of truth for movie data
 */
class MovieRepository(private val apiService: ApiService) {
    // Room databases
    private val popularMoviesDao = PopularMoviesDatabase.getDatabase().popularMoviesDao()
    private val nowPlayingMoviesDao = NowPlayingMoviesDatabase.getDatabase().nowPlayingMoviesDao()
    private val savedMoviesDao = SavedMoviesDatabase.getDatabase().savedMoviesDao()

    /**
     * Get popular movies from the API and cache in database
     * @param page Page number for pagination
     * @param fetchFromRemote Whether to fetch from remote API
     */
    suspend fun getPopularMovies(page: Int = 1, fetchFromRemote: Boolean = true): MovieResponse? {
        if (fetchFromRemote) {
            try {
                val response = apiService.getPopularMovies(page)
                response?.movies?.let { movies ->
                    // Clear previous data if it's the first page
                    if (page == 1) {
                        popularMoviesDao.clearAll()
                    }
                    // Insert new movies
                    popularMoviesDao.insertMovies(movies.map { MovieDetails.fromMovie(it) })
                }

                return response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return null
    }

    /**
     * Get cached popular movies as Flow
     */
    fun getPopularMoviesFromCache(): Flow<List<Movie>> {
        return popularMoviesDao.getAllMovies().map { entities ->
            entities?.map { it.toMovie() } ?: emptyList()
        }
    }
    
    /**
     * Check if popular movies exist in the database
     * @return true if there are movies in the database
     */
    suspend fun hasPopularMoviesInCache(): Boolean {
        return popularMoviesDao.getCount() > 0
    }
    
    /**
     * Check if a specific movie exists in the popular movies database
     * @param movieId The movie ID to check
     * @return true if the movie exists in popular movies database
     */
    suspend fun isMovieInPopularDatabase(movieId: Long): Boolean {
        return popularMoviesDao.getMovieById(movieId) != null
    }

    /**
     * Get now playing movies from the API and cache in database
     * @param page Page number for pagination
     * @param fetchFromRemote Whether to fetch from remote API
     */
    suspend fun getNowPlayingMovies(page: Int = 1, fetchFromRemote: Boolean = true): MovieResponse? {
        if (fetchFromRemote) {
            try {
                val response = apiService.getNowPlayingMovies(page)
                response?.movies?.let { movies ->
                    // Clear previous data if it's the first page
                    if (page == 1) {
                        nowPlayingMoviesDao.clearAll()
                    }
                    // Insert new movies
                    nowPlayingMoviesDao.insertMovies(movies.map { MovieDetails.fromMovie(it) })
                }
                return response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * Get cached now playing movies as Flow
     */
    fun getNowPlayingMoviesFromCache(): Flow<List<Movie>> {
        return nowPlayingMoviesDao.getAllMovies().map { entities ->
            entities?.map { it.toMovie() } ?: emptyList()
        }
    }
    
    /**
     * Check if now playing movies exist in the database
     * @return true if there are movies in the database
     */
    suspend fun hasNowPlayingMoviesInCache(): Boolean {
        return nowPlayingMoviesDao.getCount() > 0
    }
    
    /**
     * Check if a specific movie exists in the now playing movies database
     * @param movieId The movie ID to check
     * @return true if the movie exists in now playing movies database
     */
    suspend fun isMovieInNowPlayingDatabase(movieId: Long): Boolean {
        return nowPlayingMoviesDao.getMovieById(movieId) != null
    }
    
    /**
     * Get movie details by ID
     * @param movieId The movie ID
     */
    suspend fun getMovieDetails(movieId: Long): MovieDetails? {
        return apiService.getMovieDetails(movieId)
    }
    
    /**
     * Get movie details from the database
     * @param movieId The movie ID
     * @param source The source (popular, now playing, etc.)
     */
    suspend fun getMovieDetailsFromDatabase(movieId: Long, source: String): MovieDetails? {
        return when (source) {
            "source_popular" -> popularMoviesDao.getMovieById(movieId)
            "source_now_playing" -> nowPlayingMoviesDao.getMovieById(movieId)
            "source_saved" -> savedMoviesDao.getMovieById(movieId)
            else -> null
        }
    }
    
    /**
     * Search for movies based on a query
     * @param query Search query string
     * @param page Page number for pagination
     */
    suspend fun searchMovies(query: String, page: Int = 1): MovieResponse? {
        return apiService.searchMovies(query, page)
    }
    
    /**
     * Save movie details to the saved movies database
     * @param movieDetails The movie details to save
     */
    suspend fun saveMovieDetails(movieDetails: MovieDetails) {
        savedMoviesDao.insertMovie(movieDetails)
    }

    /**
     * Remove a movie from the saved movies database by ID
     * @param movieId The ID of the movie to remove
     */
    suspend fun removeSavedMovie(movieId: Long) {
        savedMoviesDao.deleteMovieById(movieId)
    }

    /**
     * Check if a movie is saved
     * @param movieId The movie ID to check
     */
    suspend fun isMovieSaved(movieId: Long): Boolean {
        return savedMoviesDao.isMovieSaved(movieId)
    }

    /**
     * Get all saved movies as Flow
     */
    fun getSavedMovies(): Flow<List<Movie>> {
        return savedMoviesDao.getAllMovies().map { entities ->
            entities?.map { it.toMovie() } ?: emptyList()
        }
    }
    
    /**
     * Update a movie in the popular movies database with full details
     * @param movieDetails The movie details to update
     */
    suspend fun updatePopularMovie(movieDetails: MovieDetails) {
        popularMoviesDao.insertMovie(movieDetails)
    }
    
    /**
     * Update a movie in the now playing movies database with full details
     * @param movieDetails The movie details to update
     */
    suspend fun updateNowPlayingMovie(movieDetails: MovieDetails) {
        nowPlayingMoviesDao.insertMovie(movieDetails)
    }
    
    /**
     * Update movie details in both popular and now playing databases if the movie exists in them
     * @param movieDetails The movie details to update
     */
    suspend fun updateMovieDetailsInDatabases(movieDetails: MovieDetails) {
        // Check if movie exists in popular database and update if it does
        if (isMovieInPopularDatabase(movieDetails.id)) {
            updatePopularMovie(movieDetails)
        }
        
        // Check if movie exists in now playing database and update if it does
        if (isMovieInNowPlayingDatabase(movieDetails.id)) {
            updateNowPlayingMovie(movieDetails)
        }
    }
} 