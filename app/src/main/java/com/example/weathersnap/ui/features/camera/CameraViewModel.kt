package com.example.weathersnap.ui.features.camera

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.util.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _captureState = MutableStateFlow<CaptureResult?>(null)
    val captureState = _captureState.asStateFlow()

    data class CaptureResult(
        val originalFile: File,
        val compressedFile: File,
        val originalSize: Long,
        val compressedSize: Long
    )

    fun processCapture(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (compressedFile, compressedSize) = ImageUtils.compressImage(context, file)
                _captureState.value = CaptureResult(
                    originalFile = file,
                    compressedFile = compressedFile,
                    originalSize = file.length(),
                    compressedSize = compressedSize
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
