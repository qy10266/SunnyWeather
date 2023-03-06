package com.sunnyweather.android.ui.weather

import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.databinding.ForecastItemBinding
import com.sunnyweather.android.iogic.model.Weather
import com.sunnyweather.android.iogic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var controller = window.decorView.windowInsetsController
//        val decorView = window.decorView
        transparencyBar(this)
//        controller?.hide(WindowInsetsCompat.Type.statusBars())
//        window.statusBarColor = Color.TRANSPARENT
//        val params = window.attributes
//        params.layoutInDisplayCutoutMode =
//            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//        window.attributes = params
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather) {
        binding.now.placeName.text =viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        binding.now.currentTemp.text = currentTempText
        binding.now.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.now.currentAQI.text = currentPM25Text
        binding.now.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        binding.forecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size

        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = ForecastItemBinding.inflate(layoutInflater,binding.root,false)
            val dateInfo = view.dateInfo
            val skyIcon = view.skyIcon
            val skyInfo = view.skyInfo
            val temperatureInfo = view.temperatureInfo
            val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDataFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            binding.forecast.forecastLayout.addView(view.forecastItem)
        }

        val lifeIndex = daily.lifeIndex
        binding.lifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.lifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        binding.lifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.lifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility = View.VISIBLE
    }

    fun transparencyBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }
    }
}