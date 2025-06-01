package com.coder.moviesmania.data.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data model representing a movie
 */
@Parcelize
data class Movie(
    @Expose
    @SerializedName("id")
    val id: Long,
    
    @Expose
    @SerializedName("title")
    val title: String?,
    
    @Expose
    @SerializedName("overview")
    val overview: String?,
    
    @Expose
    @SerializedName("poster_path")
    val posterPath: String?,
    
    @Expose
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    
    @Expose
    @SerializedName("vote_average")
    val rating: Double,
    
    @Expose
    @SerializedName("release_date")
    val releaseDate: String?
): Parcelable

/**
 * Response wrapper for movie list API
 */
@Parcelize
data class MovieResponse(
    @Expose
    @SerializedName("results")
    val movies: List<Movie>,
    
    @Expose
    @SerializedName("page")
    val page: Int,
    
    @Expose
    @SerializedName("total_pages")
    val totalPages: Int,
    
    @Expose
    @SerializedName("total_results")
    val totalResults: Int
) : Parcelable