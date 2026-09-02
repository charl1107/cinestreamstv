package com.cinestreamtv.tv.ui.browse

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.material3.*
import com.cinestreamtv.core.domain.model.MediaItem
import com.cinestreamtv.tv.ui.home.MediaCard

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BrowseScreen(
    providerName: String,
    onMediaClick: (MediaItem) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) { Text("← Back") }
            Text(
                text = providerName,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Placeholder - would be connected to a ViewModel
        TvLazyVerticalGrid(
            columns = TvGridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Items would come from BrowseViewModel
        }
    }
}
