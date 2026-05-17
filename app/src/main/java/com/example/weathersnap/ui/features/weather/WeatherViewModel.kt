package com.example.weathersnap.ui.features.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.domain.models.City
import com.example.weathersnap.domain.models.WeatherData
import com.example.weathersnap.domain.repository.WeatherRepository
import com.example.weathersnap.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<City>>>(Resource.Loading())
    val searchState = _searchState.asStateFlow()

    private val _weatherState = MutableStateFlow<Resource<WeatherData>?>(null)
    val weatherState = _weatherState.asStateFlow()

    private var searchJob: Job? = null

    fun onCityQueryChanged(query: String) {
        // Debounce logic: Wait 500ms before calling API
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) 
            if (query.length >= 3) {
                _searchState.value = Resource.Loading()
                _searchState.value = repository.searchCities(query)
            }
        }
    }

    fun onCitySelected(city: City) {
        _searchState.value = Resource.Success(emptyList())
        viewModelScope.launch {
            _weatherState.value = Resource.Loading()
            _weatherState.value = repository.getWeather(city.latitude, city.longitude, city.name)
        }
    }
}
