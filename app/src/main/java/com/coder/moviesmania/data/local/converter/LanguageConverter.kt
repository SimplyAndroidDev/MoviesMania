package com.coder.moviesmania.data.local.converter

import androidx.room.TypeConverter
import com.coder.moviesmania.data.model.Language
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TypeConverter for converting Language list to and from String for Room database storage
 */
class LanguageConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromLanguageList(languages: List<Language>?): String? {
        if (languages.isNullOrEmpty()) {
            return null
        }

        return gson.toJson(languages)
    }

    @TypeConverter
    fun toLanguageList(languagesString: String?): List<Language>? {
        if (languagesString.isNullOrEmpty()) {
            return null
        }

        val type = object : TypeToken<List<Language>>() {}.type
        return gson.fromJson(languagesString, type)
    }
} 