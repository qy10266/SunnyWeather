package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.sunnyweather.android.iogic.model.Location

class SunnyWeatherApplication : Application() {

    companion object {

        const val TOKEN = "uqI35bA4EzlGcIvR"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}