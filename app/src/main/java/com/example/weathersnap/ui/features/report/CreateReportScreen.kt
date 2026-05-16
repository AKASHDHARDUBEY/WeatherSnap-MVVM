package com.example.weathersnap.ui.features.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.weathersnap.ui.navigation.Screen
import com.example.weathersnap.ui.theme.AccentGreen
import com.example.weathersnap.ui.theme.CardBackground
import com.example.weathersnap.ui.theme.DarkBackground
import com.example.weathersnap.ui.theme.SurfaceGreen
import com.example.weathersnap.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    navController: NavController,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.loadDraft()
    }

    val capturedImage = navController.previousBackStackEntry?.savedStateHandle?.get<String>("captured_image")
    val origSize = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("original_size")
    val compSize = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("compressed_size")

    LaunchedEffect(capturedImage) {
        capturedImage?.let {
            viewModel.imagePath = it
            viewModel.originalSize = origSize ?: 0L
            viewModel.compressedSize = compSize ?: 0L
            viewModel.updateDraft()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Create Report", color = Color.White, style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back", color = Color.White)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = viewModel.cityName,
                modifier = Modifier.padding(16.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(SurfaceGreen, RoundedCornerShape(12.dp))
                .clickable { navController.navigate(Screen.Camera.route) },
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.imagePath != null) {
                AsyncImage(
                    model = viewModel.imagePath,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                    Text("Tap to Capture Photo", color = Color.White)
                }
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Camera.route) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            Text("Capture Photo", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.notes,
            onValueChange = {
                viewModel.notes = it
                viewModel.updateDraft()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Field Notes", color = TextSecondary) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AccentGreen,
                unfocusedBorderColor = TextSecondary,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.saveFinalReport {
                    navController.navigate(Screen.SavedReports.route) {
                        popUpTo(Screen.Weather.route) { inclusive = false }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            Text("Save Report", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
