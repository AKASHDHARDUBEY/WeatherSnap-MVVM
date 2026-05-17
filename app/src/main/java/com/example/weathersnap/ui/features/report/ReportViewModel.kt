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

    private val rawData: String = savedStateHandle.get<String>("weatherData") ?: "Unknown City|0.0|Unknown"
    private val parts = rawData.split("|")

    val cityName: String = parts.getOrElse(0) { "Unknown City" }
    val temp: Double = parts.getOrElse(1) { "0.0" }.toDoubleOrNull() ?: 0.0
    val condition: String = parts.getOrElse(2) { "Unknown" }

    private var draftId: Int? = null
    private var draftSaveInProgress = false

    fun loadDraft() {
        viewModelScope.launch {
            try {
                val draft = repository.getDraftForCity(cityName)
                draft?.let {
                    draftId = it.id
                    notes = it.notes
                    imagePath = it.imagePath
                    originalSize = it.originalSize
                    compressedSize = it.compressedSize
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateDraft() {
        if (draftSaveInProgress) return
        draftSaveInProgress = true
        viewModelScope.launch {
            try {
                val report = ReportEntity(
                    id = draftId ?: 0,
                    cityName = cityName,
                    temp = temp,
                    condition = condition,
                    notes = notes,
                    imagePath = imagePath ?: "",
                    originalSize = originalSize,
                    compressedSize = compressedSize,
                    isDraft = true
                )
                repository.saveReport(report)
                if (draftId == null) {
                    val saved = repository.getDraftForCity(cityName)
                    draftId = saved?.id
                }
            } finally {
                draftSaveInProgress = false
            }
        }
    }

    fun saveFinalReport(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (draftId != null) {
                repository.deleteReportById(draftId!!)
            }
            val finalReport = ReportEntity(
                id = 0,
                cityName = cityName,
                temp = temp,
                condition = condition,
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
