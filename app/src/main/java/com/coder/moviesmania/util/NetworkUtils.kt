package com.coder.moviesmania.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.coder.moviesmania.R
import com.coder.moviesmania.MoviesManiaApplication

/**
 * Utility object for network-related operations
 */
object NetworkUtils {
    
    /**
     * Check if the device has an active internet connection
     * @return Boolean indicating whether the device has internet connectivity
     */
    fun isNetworkAvailable(): Boolean {
        val context = MoviesManiaApplication.instance
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Check for WiFi connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Check for cellular connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // Check for ethernet connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true

            // Check for VPN connectivity (as VPN can use any of the above transports)
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true

            // No internet connection
            else -> false
        }
    }

    fun getNoInternetErrorMessage(): Int {
        return R.string.error_no_internet
    }
} 