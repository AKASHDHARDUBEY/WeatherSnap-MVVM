package com.example.weathersnap.ui.features.report

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.local.entities.ReportEntity
import com.example.weathersnap.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var notes by mutableStateOf("")
    var imagePath by mutableStateOf<String?>(null)
    var originalSize by mutableStateOf(0L)
    var compressedSize by mutableStateOf(0L)
    
    val cityName: String = savedStateHandle.get<String>("weatherData") ?: "Unknown City"

    private var draftId: Int? = null

    fun loadDraft() {
        viewModelScope.launch {
            try {
                val draft = (repository as? Any)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateDraft() {
        viewModelScope.launch {
            val report = ReportEntity(
                id = draftId ?: 0,
                cityName = cityName,
                notes = notes,
                imagePath = imagePath ?: "",
                originalSize = originalSize,
                compressedSize = compressedSize,
                isDraft = true
            )
            repository.saveReport(report)
        }
    }

    fun saveFinalReport(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val finalReport = ReportEntity(
                id = draftId ?: 0,
                cityName = cityName,
                notes = notes,
                imagePath = imagePath ?: "",
                originalSize = originalSize,
                compressedSize = compressedSize,
                isDraft = false
            )
            repository.saveReport(finalReport)
            onSuccess()
        }
    }
}
