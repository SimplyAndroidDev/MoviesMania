package com.coder.moviesmania.data.local.converter

import androidx.room.TypeConverter
import com.coder.moviesmania.data.model.ProductionCompany
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TypeConverter for converting ProductionCompany list to and from String for Room database storage
 */
class ProductionCompanyConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromProductionCompanyList(companies: List<ProductionCompany>?): String? {
        if (companies.isNullOrEmpty()) {
            return null
        }

        return gson.toJson(companies)
    }

    @TypeConverter
    fun toProductionCompanyList(companiesString: String?): List<ProductionCompany>? {
        if (companiesString.isNullOrEmpty()) {
            return null
        }

        val type = object : TypeToken<List<ProductionCompany>>() {}.type
        return gson.fromJson(companiesString, type)
    }
} 