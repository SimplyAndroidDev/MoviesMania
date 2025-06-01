package com.coder.moviesmania.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coder.moviesmania.data.model.MovieDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedMoviesDao {
    @Query("SELECT * FROM MovieDetails ORDER BY timestamp DESC")
    fun getAllMovies(): Flow<List<MovieDetails>?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieDetails)
    
    @Query("DELETE FROM MovieDetails WHERE id = :movieId")
    suspend fun deleteMovieById(movieId: Long)
    
    @Query("DELETE FROM MovieDetails")
    suspend fun clearAll()
    
    @Query("SELECT * FROM MovieDetails WHERE id = :movieId")
    suspend fun getMovieById(movieId: Long): MovieDetails?
    
    @Query("SELECT EXISTS(SELECT 1 FROM MovieDetails WHERE id = :movieId LIMIT 1)")
    suspend fun isMovieSaved(movieId: Long): Boolean
} 