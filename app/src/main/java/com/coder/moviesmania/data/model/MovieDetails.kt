package com.coder.moviesmania.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.coder.moviesmania.data.local.converter.GenreConverter
import com.coder.moviesmania.data.local.converter.LanguageConverter
import com.coder.moviesmania.data.local.converter.ProductionCompanyConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class MovieDetails(
    @PrimaryKey
    @Expose
    @SerializedName("id")
    val id: Long,
    
    @Expose
    @SerializedName("title")
    val title: String?,

    @Expose
    @SerializedName("original_title")
    val originalTitle: String?,

    @Expose
    @SerializedName("overview")
    val overview: String?,
    
    @Expose
    @SerializedName("tagline")
    val tagline: String?,

    @Expose
    @SerializedName("poster_path")
    val posterPath: String?,

    @Expose
    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @Expose
    @SerializedName("release_date")
    val releaseDate: String?,

    @Expose
    @SerializedName("vote_average")
    val rating: Double?,

    @Expose
    @SerializedName("vote_count")
    val voteCount: Int = 0,

    @Expose
    @SerializedName("runtime")
    val runtime: Int = 0,

    @Expose
    @SerializedName("genres")
    @TypeConverters(GenreConverter::class)
    val genres: List<Genre>? = emptyList(),

    @Expose
    @SerializedName("production_companies")
    @TypeConverters(ProductionCompanyConverter::class)
    val productionCompanies: List<ProductionCompany>? = emptyList(),

    @Expose
    @SerializedName("spoken_languages")
    @TypeConverters(LanguageConverter::class)
    val spokenLanguages: List<Language>? = emptyList(),

    @Expose
    @SerializedName("status")
    val status: String = "",
    
    @Expose
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    /**
     * Convert to Movie domain model
     */
    fun toMovie() = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        rating = rating ?: 0.0
    )
    
    companion object {
        /**
         * Create MovieDetails from Movie with default values
         */
        fun fromMovie(movie: Movie) = MovieDetails(
            id = movie.id,
            title = movie.title,
            originalTitle = movie.title,
            overview = movie.overview,
            tagline = "",
            posterPath = movie.posterPath,
            backdropPath = movie.backdropPath,
            releaseDate = movie.releaseDate,
            rating = movie.rating,
            voteCount = 0,
            runtime = 0,
            genres = null,
            productionCompanies = null,
            spokenLanguages = null,
            status = ""
        )
    }
}

@Parcelize
data class Genre(
    @Expose
    @SerializedName("id")
    val id: Int,
    
    @Expose
    @SerializedName("name")
    val name: String
) : Parcelable

@Parcelize
data class ProductionCompany(
    @Expose
    @SerializedName("id")
    val id: Int,
    
    @Expose
    @SerializedName("name")
    val name: String?,

    @Expose
    @SerializedName("logo_path")
    val logoPath: String?,

    @Expose
    @SerializedName("origin_country")
    val originCountry: String?
) : Parcelable

@Parcelize
data class Language(
    @Expose
    @SerializedName("english_name")
    val englishName: String?,

    @Expose
    @SerializedName("iso_639_1")
    val iso: String?,

    @Expose
    @SerializedName("name")
    val name: String?
) : Parcelable