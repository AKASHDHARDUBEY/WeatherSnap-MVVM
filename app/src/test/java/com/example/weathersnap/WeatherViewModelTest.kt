package com.example.weathersnap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weathersnap.domain.models.City
import com.example.weathersnap.domain.repository.WeatherRepository
import com.example.weathersnap.ui.features.weather.WeatherViewModel
import com.example.weathersnap.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel
    private val repository: WeatherRepository = mockk()
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when city query is less than 3 letters, search state should be Error`() = runTest {
        val query = "Lo"

        viewModel.onCityQueryChanged(query)
        advanceUntilIdle() // Wait for the debounce delay(500)

        assert(viewModel.searchState.value is Resource.Error)
        assert((viewModel.searchState.value as Resource.Error).message == "Enter at least 3 letters")
    }

    @Test
    fun `when API returns success, search state should be Success`() = runTest {
        val query = "London"
        val mockCities = listOf(City("London", 51.5, -0.12, "UK"))
        coEvery { repository.searchCities(query) } returns Resource.Success(mockCities)

        viewModel.onCityQueryChanged(query)
        advanceUntilIdle()

        assert(viewModel.searchState.value is Resource.Success)
        assert((viewModel.searchState.value as Resource.Success).data?.size == 1)
    }
}
