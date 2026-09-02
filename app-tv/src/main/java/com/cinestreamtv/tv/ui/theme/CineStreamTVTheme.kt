package com.cinestreamtv.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CineStreamTVTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme()
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
