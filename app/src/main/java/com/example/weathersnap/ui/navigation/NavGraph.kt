package com.example.weathersnap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weathersnap.ui.features.camera.CameraScreen
import com.example.weathersnap.ui.features.report.CreateReportScreen
import com.example.weathersnap.ui.features.saved.SavedReportsScreen
import com.example.weathersnap.ui.features.weather.WeatherScreen

sealed class Screen(val route: String) {
    object Weather : Screen("weather_screen")
    object CreateReport : Screen("create_report_screen/{weatherData}")
    object Camera : Screen("camera_screen")
    object SavedReports : Screen("saved_reports_screen")
}

@Composable
fun WeatherNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route
    ) {
        composable(Screen.Weather.route) {
            WeatherScreen(
                onNavigateToCreateReport = { data -> 
                    navController.navigate("create_report_screen/$data") 
                },
                onNavigateToReports = { 
                    navController.navigate(Screen.SavedReports.route) 
                }
            )
        }
        composable("create_report_screen/{weatherData}") {
            CreateReportScreen(navController = navController)
        }
        composable(Screen.Camera.route) {
            CameraScreen(navController = navController)
        }
        composable(Screen.SavedReports.route) {
            SavedReportsScreen(navController = navController)
        }
    }
}
