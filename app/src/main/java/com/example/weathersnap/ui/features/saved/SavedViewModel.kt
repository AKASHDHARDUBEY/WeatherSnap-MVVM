package com.example.weathersnap.ui.features.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.local.entities.ReportEntity
import com.example.weathersnap.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _reports = MutableStateFlow<List<ReportEntity>>(emptyList())
    val reports = _reports.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _reports.value = repository.getAllReports()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
