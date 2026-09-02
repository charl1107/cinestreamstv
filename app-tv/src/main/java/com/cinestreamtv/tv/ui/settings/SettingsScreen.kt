package com.cinestreamtv.tv.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.*

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    onExtensionsClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        Row {
            Button(onClick = onBack) { Text("← Back") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TvLazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(onClick = onExtensionsClick, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Extensions", style = MaterialTheme.typography.titleMedium)
                            Text("Manage content providers", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("→")
                    }
                }
            }
            item {
                Card(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Playback", style = MaterialTheme.typography.titleMedium)
                            Text("Default quality, subtitles, speed", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("→")
                    }
                }
            }
            item {
                Card(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("About", style = MaterialTheme.typography.titleMedium)
                            Text("CineStreamTV v1.0.0", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
