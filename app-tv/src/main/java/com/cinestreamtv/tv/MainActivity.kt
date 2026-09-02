package com.cinestreamtv.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cinestreamtv.tv.navigation.CineStreamNavHost
import com.cinestreamtv.tv.ui.theme.CineStreamTVTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CineStreamTVTheme {
                CineStreamNavHost()
            }
        }
    }
}
