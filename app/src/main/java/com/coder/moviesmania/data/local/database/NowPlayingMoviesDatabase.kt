package com.coder.moviesmania.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coder.moviesmania.MoviesManiaApplication
import com.coder.moviesmania.data.local.converter.GenreConverter
import com.coder.moviesmania.data.local.converter.LanguageConverter
import com.coder.moviesmania.data.local.converter.ProductionCompanyConverter
import com.coder.moviesmania.data.local.dao.NowPlayingMoviesDao
import com.coder.moviesmania.data.model.MovieDetails

@Database(entities = [MovieDetails::class], version = 1, exportSchema = false)
@TypeConverters(GenreConverter::class, ProductionCompanyConverter::class, LanguageConverter::class)
abstract class NowPlayingMoviesDatabase : RoomDatabase() {
    
    abstract fun nowPlayingMoviesDao(): NowPlayingMoviesDao
    
    companion object {
        @Volatile
        private var INSTANCE: NowPlayingMoviesDatabase? = null
        
        fun getDatabase(): NowPlayingMoviesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    MoviesManiaApplication.instance,
                    NowPlayingMoviesDatabase::class.java,
                    "now_playing_movies_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 