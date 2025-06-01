package com.coder.moviesmania.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coder.moviesmania.data.model.MovieDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface PopularMoviesDao {
    @Query("SELECT * FROM MovieDetails")
    fun getAllMovies(): Flow<List<MovieDetails>?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieDetails>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieDetails)
    
    @Query("DELETE FROM MovieDetails")
    suspend fun clearAll()
    
    @Query("SELECT * FROM MovieDetails WHERE id = :movieId")
    suspend fun getMovieById(movieId: Long): MovieDetails?
    
    @Query("SELECT COUNT(*) FROM MovieDetails")
    suspend fun getCount(): Int
} 