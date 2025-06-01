package com.coder.moviesmania.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coder.moviesmania.MoviesManiaApplication
import com.coder.moviesmania.data.local.converter.GenreConverter
import com.coder.moviesmania.data.local.converter.LanguageConverter
import com.coder.moviesmania.data.local.converter.ProductionCompanyConverter
import com.coder.moviesmania.data.local.dao.PopularMoviesDao
import com.coder.moviesmania.data.model.MovieDetails

@Database(entities = [MovieDetails::class], version = 1, exportSchema = false)
@TypeConverters(GenreConverter::class, ProductionCompanyConverter::class, LanguageConverter::class)
abstract class PopularMoviesDatabase : RoomDatabase() {
    
    abstract fun popularMoviesDao(): PopularMoviesDao
    
    companion object {
        @Volatile
        private var INSTANCE: PopularMoviesDatabase? = null
        
        fun getDatabase(): PopularMoviesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    MoviesManiaApplication.instance,
                    PopularMoviesDatabase::class.java,
                    "popular_movies_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 