package com.coder.moviesmania.data.local.converter

import androidx.room.TypeConverter
import com.coder.moviesmania.data.model.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TypeConverter for converting Genre list to and from String for Room database storage
 */
class GenreConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromGenreList(genres: List<Genre?>?): String? {
        if (genres.isNullOrEmpty()) {
            return null
        }

        return gson.toJson(genres)
    }

    @TypeConverter
    fun toGenreList(genresString: String?): List<Genre?>? {
        if (genresString.isNullOrEmpty()) {
            return null
        }

        val type = object : TypeToken<List<Genre?>?>() {}.type
        return gson.fromJson(genresString, type)
    }
} 