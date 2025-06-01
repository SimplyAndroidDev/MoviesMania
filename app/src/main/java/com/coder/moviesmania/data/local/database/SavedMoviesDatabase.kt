package com.coder.moviesmania.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coder.moviesmania.MoviesManiaApplication
import com.coder.moviesmania.data.local.converter.GenreConverter
import com.coder.moviesmania.data.local.converter.LanguageConverter
import com.coder.moviesmania.data.local.converter.ProductionCompanyConverter
import com.coder.moviesmania.data.local.dao.SavedMoviesDao
import com.coder.moviesmania.data.model.MovieDetails

@Database(entities = [MovieDetails::class], version = 1, exportSchema = false)
@TypeConverters(GenreConverter::class, ProductionCompanyConverter::class, LanguageConverter::class)
abstract class SavedMoviesDatabase : RoomDatabase() {
    
    abstract fun savedMoviesDao(): SavedMoviesDao
    
    companion object {
        @Volatile
        private var INSTANCE: SavedMoviesDatabase? = null
        
        fun getDatabase(): SavedMoviesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    MoviesManiaApplication.instance,
                    SavedMoviesDatabase::class.java,
                    "saved_movies_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 