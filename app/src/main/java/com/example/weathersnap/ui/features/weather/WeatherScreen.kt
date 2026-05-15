package com.example.weathersnap.ui.features.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weathersnap.domain.models.City
import com.example.weathersnap.domain.models.WeatherData
import com.example.weathersnap.ui.theme.*
import com.example.weathersnap.util.Resource
import com.example.weathersnap.util.shimmerEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
    onNavigateToCreateReport: (String) -> Unit,
    onNavigateToReports: () -> Unit
) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    var cityQuery by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(searchState) {
        if (searchState is Resource.Error) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = searchState.message ?: "Something went wrong",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WeatherSnap",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onNavigateToReports,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reports", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = cityQuery,
                onValueChange = {
                    cityQuery = it
                    viewModel.onCityQueryChanged(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("City", color = TextSecondary) },
                placeholder = { Text("Search city...", color = TextSecondary) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AccentGreen,
                    unfocusedBorderColor = TextSecondary,
                    cursorColor = AccentGreen,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                trailingIcon = {
                    IconButton(onClick = { viewModel.onCitySelected(searchState.data?.firstOrNull() ?: return@IconButton) }) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = AccentGreen)
                    }
                }
            )
            
            Text(
                text = "Enter more than 2 letters to start city suggestions",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
            )

            AnimatedVisibility(
                visible = searchState is Resource.Success && !searchState.data.isNullOrEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .background(CardBackground, RoundedCornerShape(8.dp))
                ) {
                    items(searchState.data ?: emptyList()) { city ->
                        CitySuggestionItem(city) {
                            cityQuery = city.name
                            viewModel.onCitySelected(city)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = weatherState is Resource.Success,
                enter = expandVertically() + fadeIn()
            ) {
                WeatherResultCard(
                    weather = weatherState.data!!,
                    onCreateReport = { onNavigateToCreateReport(weatherState.data!!.cityName) }
                )
            }

            if (searchState is Resource.Loading) {
                Column {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(vertical = 4.dp)
                                .shimmerEffect()
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CitySuggestionItem(city: City, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
            .background(CardBackground, RoundedCornerShape(4.dp))
    ) {
        Text(text = "${city.name}, ${city.country}", color = Color.White)
    }
}

@Composable
fun WeatherResultCard(weather: WeatherData, onCreateReport: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = weather.cityName, style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(text = "${weather.temperature}°C", style = MaterialTheme.typography.headlineMedium, color = AccentGreen, fontWeight = FontWeight.Bold)
            }
            Text(text = weather.condition, color = TextSecondary)
            
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = SurfaceGreen)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem("Humidity", "${weather.humidity}%")
                WeatherDetailItem("Wind", "${weather.windSpeed} m/s")
                WeatherDetailItem("Pressure", "${weather.pressure} hPa")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onCreateReport,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Create Report", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Text(text = label, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        Text(text = value, color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
