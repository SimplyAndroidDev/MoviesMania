package com.coder.moviesmania

import android.app.Application

class MoviesManiaApplication: Application() {
    companion object {
        lateinit var instance: MoviesManiaApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}