package com.cinestreamtv.tv.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.material3.*
import com.cinestreamtv.core.domain.model.MediaItem
import com.cinestreamtv.tv.ui.home.MediaCard

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchScreen(
    onMediaClick: (MediaItem) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        // Search Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack) { Text("← Back") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Search",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Query display (TV keyboard would handle input)
        Text(
            text = if (uiState.query.isEmpty()) "Type to search..." else "Results for: ${uiState.query}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Results Grid
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Searching...")
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error ?: "", color = Color.Red)
                }
            }
            uiState.results.isEmpty() && uiState.query.length >= 2 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results found")
                }
            }
            else -> {
                TvLazyVerticalGrid(
                    columns = TvGridCells.Adaptive(160.dp),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.results) { item ->
                        MediaCard(item = item, onClick = { onMediaClick(item) })
                    }
                }
            }
        }
    }
}
