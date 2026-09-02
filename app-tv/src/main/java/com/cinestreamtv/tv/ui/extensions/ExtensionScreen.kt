package com.cinestreamtv.tv.ui.extensions

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.*
import coil.compose.AsyncImage

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ExtensionScreen(
    onBack: () -> Unit,
    viewModel: ExtensionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Button(onClick = onBack) { Text("← Back") }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Extensions",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Button(onClick = { viewModel.refreshPlugins() }) {
                Text("Refresh")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Repos section
        Text(
            text = "Repositories",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        uiState.repos.forEach { repo ->
            Card(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = repo.name, fontWeight = FontWeight.Bold)
                        Text(
                            text = repo.url,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    if (!repo.isDefault) {
                        Button(onClick = { viewModel.removeRepo(repo.url) }) {
                            Text("Remove")
                        }
                    } else {
                        Text("Default", color = Color.Green)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Available plugins
        Text(
            text = "Available Extensions (${uiState.availablePlugins.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Loading extensions...") }
            }
            uiState.error != null -> {
                Text(text = uiState.error ?: "", color = Color.Red)
            }
            else -> {
                TvLazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.availablePlugins) { plugin ->
                        val installed = viewModel.isInstalled(plugin.internalName)
                        val isInstalling = uiState.installingPlugin == plugin.internalName

                        Card(
                            onClick = {
                                if (!installed && !isInstalling) {
                                    viewModel.installPlugin(plugin)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (plugin.iconUrl != null) {
                                        AsyncImage(
                                            model = plugin.iconUrl,
                                            contentDescription = plugin.name,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = plugin.name,
                                            fontWeight = FontWeight.Bold
                                        )
                                        plugin.description?.let {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                        }
                                        Text(
                                            text = "v${plugin.version} • ${plugin.language ?: "en"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                                when {
                                    isInstalling -> Text("Installing...", color = Color.Yellow)
                                    installed -> {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("Installed", color = Color.Green)
                                            Button(onClick = {
                                                viewModel.uninstallPlugin(plugin.internalName)
                                            }) {
                                                Text("Remove")
                                            }
                                        }
                                    }
                                    else -> Button(onClick = {
                                        viewModel.installPlugin(plugin)
                                    }) {
                                        Text("Install")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
