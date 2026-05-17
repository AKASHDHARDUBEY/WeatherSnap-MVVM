package com.example.weathersnap.ui.features.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.weathersnap.util.ImageUtils

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

    val currentEntry = navController.currentBackStackEntry
    val capturedImage = currentEntry?.savedStateHandle?.get<String>("captured_image")
    val origSize = currentEntry?.savedStateHandle?.get<Long>("original_size")
    val compSize = currentEntry?.savedStateHandle?.get<Long>("compressed_size")

    LaunchedEffect(capturedImage) {
        capturedImage?.let {
            viewModel.imagePath = it
            viewModel.originalSize = origSize ?: 0L
            viewModel.compressedSize = compSize ?: 0L
            viewModel.updateDraft()
            currentEntry?.savedStateHandle?.remove<String>("captured_image")
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = viewModel.cityName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${viewModel.temp}\u00B0C",
                        color = AccentGreen,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = viewModel.condition,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Text("Tap to Capture Photo", color = Color.White)
                }
            }
        }

        if (viewModel.originalSize > 0) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Original: ${ImageUtils.getFileSizeInKB(viewModel.originalSize)}",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    "Compressed: ${ImageUtils.getFileSizeInKB(viewModel.compressedSize)}",
                    color = AccentGreen,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Camera.route) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            Text("Capture Photo", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(12.dp))

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
