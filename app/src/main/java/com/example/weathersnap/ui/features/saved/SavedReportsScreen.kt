package com.example.weathersnap.ui.features.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.weathersnap.data.local.entities.ReportEntity
import com.example.weathersnap.ui.theme.AccentGreen
import com.example.weathersnap.ui.theme.CardBackground
import com.example.weathersnap.ui.theme.DarkBackground
import com.example.weathersnap.ui.theme.TextSecondary
import com.example.weathersnap.util.ImageUtils

@Composable
fun SavedReportsScreen(
    navController: NavController,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Saved Reports", color = Color.White, style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back", color = Color.White)
            }
        }

        if (reports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reports saved yet", color = TextSecondary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(reports) { report ->
                    ReportItem(report)
                }
            }
        }
    }
}

@Composable
fun ReportItem(report: ReportEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = report.imagePath,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = report.cityName, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = report.notes, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Orig: ${ImageUtils.getFileSizeInKB(report.originalSize)}", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                Text("Comp: ${ImageUtils.getFileSizeInKB(report.compressedSize)}", color = AccentGreen, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
