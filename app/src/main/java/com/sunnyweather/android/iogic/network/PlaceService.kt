package com.sunnyweather.android.iogic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.iogic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("v2/place?tokrn=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>

}