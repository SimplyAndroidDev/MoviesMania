package com.coder.moviesmania.data.network

import android.content.Context
import com.coder.moviesmania.data.network.api.ApiService
import com.coder.moviesmania.data.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton class for creating and providing Retrofit client instances
 * Uses lazy initialization to ensure thread safety and performance
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val TIMEOUT = 15L
    
    // Authorization token for TMDB API
    private const val AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1NzNjNWM3ZWVmODhkZWIyODliZDAwNzZhMzkzZWE0MSIsIm5iZiI6MTc0ODY4MjIyNi43MDcsInN1YiI6IjY4M2FjNWYyNTJjMWJiNTdjZjdmMzZhYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.JVvvJBtcCCaZICJfj1bamDizSe1U4YGvR7aWlH0VK3E"
    
    // Header interceptor to add authorization and content type headers
    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", AUTH_TOKEN)
            .header("Accept", "application/json")
            .method(original.method, original.body)
        
        val request = requestBuilder.build()
        chain.proceed(request)
    }
    
    // Lazy initialized OkHttpClient instance - only created when first accessed
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(headerInterceptor) // Add the header interceptor first
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    // Lazy initialized Gson instance - only created when first accessed
    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create()
    }
    
    // Lazy initialized Retrofit instance - only created when first accessed
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    // Lazy initialized ApiService instance - only created when first accessed
    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    
    /**
     * Returns the ApiService instance, creating it only if it hasn't been created yet
     */
    fun createApiService(): ApiService = apiService
} 