package com.cinestreamtv.tv.ui.detail

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
import com.cinestreamtv.core.domain.model.MediaType
import com.cinestreamtv.tv.ui.home.MediaCard

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DetailScreen(
    providerName: String,
    url: String,
    onPlayClick: (data: String, title: String, mediaId: String) -> Unit,
    onMediaClick: (MediaItem) -> Unit,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(url, providerName) {
        viewModel.loadDetails(url, providerName)
    }

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading details...")
            }
        }
        uiState.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.error ?: "", color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) { Text("Go Back") }
                }
            }
        }
        uiState.details != null -> {
            val details = uiState.details!!
            TvLazyColumn(modifier = Modifier.fillMaxSize()) {
                // Backdrop with details
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
                        AsyncImage(
                            model = details.item.backdropUrl ?: details.item.posterUrl,
                            contentDescription = details.item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Black.copy(alpha = 0.9f), Color.Transparent)
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 48.dp)
                                .widthIn(max = 500.dp)
                        ) {
                            Button(onClick = onBack) { Text("← Back") }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = details.item.title,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                details.item.year?.let { Text("$it", color = Color.White.copy(alpha = 0.7f)) }
                                details.item.rating?.let { Text("★ $it", color = Color.Yellow) }
                                details.duration?.let { Text("${it}min", color = Color.White.copy(alpha = 0.7f)) }
                            }
                            if (details.genres.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = details.genres.joinToString(" • "),
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            details.plot?.let { plot ->
                                Text(
                                    text = plot,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(onClick = {
                                    if (details.item.type == MediaType.MOVIE) {
                                        onPlayClick(details.data, details.item.title, details.item.id)
                                    }
                                }) {
                                    Text("▶  Play")
                                }
                                OutlinedButton(onClick = { viewModel.toggleBookmark() }) {
                                    Text(if (uiState.isBookmarked) "★ Bookmarked" else "☆ Bookmark")
                                }
                            }
                        }
                    }
                }

                // Episodes (for series)
                if (details.seasons.isNotEmpty()) {
                    item {
                        Text(
                            text = "Episodes",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp)
                        )
                    }
                    // Season tabs
                    item {
                        TvLazyRow(
                            contentPadding = PaddingValues(horizontal = 48.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(details.seasons) { season ->
                                val isSelected = season.seasonNumber == uiState.selectedSeason
                                Button(
                                    onClick = { viewModel.selectSeason(season.seasonNumber) }
                                ) {
                                    Text(
                                        text = season.name ?: "Season ${season.seasonNumber}",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                    // Episodes for selected season
                    val selectedSeasonEpisodes = details.seasons
                        .find { it.seasonNumber == uiState.selectedSeason }
                        ?.episodes ?: emptyList()
                    item {
                        TvLazyRow(
                            contentPadding = PaddingValues(horizontal = 48.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(selectedSeasonEpisodes) { episode ->
                                Card(
                                    onClick = {
                                        onPlayClick(
                                            episode.data,
                                            "${details.item.title} S${episode.season}E${episode.episode}",
                                            "${details.item.id}_s${episode.season}e${episode.episode}"
                                        )
                                    },
                                    modifier = Modifier.width(260.dp).height(160.dp)
                                ) {
                                    Box {
                                        if (episode.thumbnailUrl != null) {
                                            AsyncImage(
                                                model = episode.thumbnailUrl,
                                                contentDescription = episode.title,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Column(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.7f))
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "E${episode.episode}: ${episode.title ?: "Episode ${episode.episode}"}",
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Recommendations
                if (details.recommendations.isNotEmpty()) {
                    item {
                        Text(
                            text = "You Might Also Like",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 12.dp)
                        )
                    }
                    item {
                        TvLazyRow(
                            contentPadding = PaddingValues(horizontal = 48.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(details.recommendations) { item ->
                                MediaCard(item = item, onClick = { onMediaClick(item) })
                            }
                        }
                    }
                }
            }
        }
    }
}
