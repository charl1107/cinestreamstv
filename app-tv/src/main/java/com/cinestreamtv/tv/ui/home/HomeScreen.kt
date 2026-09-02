package com.cinestreamtv.tv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.cinestreamtv.core.domain.model.MediaItem

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    onMediaClick: (MediaItem) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onExtensionsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TvLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Navigation Bar
        item {
            TopBar(
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick,
                onExtensionsClick = onExtensionsClick
            )
        }

        // Hero Banner
        if (uiState.homePageData.heroItems.isNotEmpty()) {
            item {
                HeroBanner(
                    items = uiState.homePageData.heroItems,
                    onItemClick = onMediaClick
                )
            }
        }

        // Continue Watching Row
        if (uiState.continueWatching.isNotEmpty()) {
            item {
                Text(
                    text = "Continue Watching",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 12.dp)
                )
            }
            item {
                TvLazyRow(
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.continueWatching) { historyItem ->
                        ContinueWatchingCard(
                            item = historyItem,
                            onClick = { /* Navigate to player with resume */ }
                        )
                    }
                }
            }
        }

        // Content Rows from providers
        uiState.homePageData.rows.forEach { row ->
            item {
                Text(
                    text = row.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 12.dp)
                )
            }
            item {
                TvLazyRow(
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(row.items) { mediaItem ->
                        MediaCard(
                            item = mediaItem,
                            onClick = { onMediaClick(mediaItem) }
                        )
                    }
                }
            }
        }

        // Loading State
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Loading content...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Error State
        uiState.error?.let { error ->
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadHomePage() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopBar(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onExtensionsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CineStreamTV",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSearchClick) { Text("Search") }
            Button(onClick = onExtensionsClick) { Text("Extensions") }
            Button(onClick = onSettingsClick) { Text("Settings") }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HeroBanner(
    items: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val selectedItem = items.getOrNull(selectedIndex) ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        AsyncImage(
            model = selectedItem.backdropUrl ?: selectedItem.posterUrl,
            contentDescription = selectedItem.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
        )
        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 32.dp)
        ) {
            Text(
                text = selectedItem.title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            selectedItem.overview?.let { overview ->
                Text(
                    text = overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 500.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onItemClick(selectedItem) }) {
                    Text("▶  Play")
                }
                OutlinedButton(onClick = { onItemClick(selectedItem) }) {
                    Text("More Info")
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MediaCard(
    item: MediaItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(160.dp).height(240.dp)
    ) {
        Box {
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    item.year?.let { year ->
                        Text(
                            text = year.toString(),
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContinueWatchingCard(
    item: com.cinestreamtv.core.domain.model.WatchHistoryItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(220.dp).height(140.dp)
    ) {
        Box {
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Progress bar at bottom
            val progress = if (item.totalDuration > 0) {
                item.lastPosition.toFloat() / item.totalDuration.toFloat()
            } else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Gray.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(Color.Red)
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
