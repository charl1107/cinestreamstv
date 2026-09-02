package com.cinestreamtv.tv.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cinestreamtv.tv.ui.home.HomeScreen
import com.cinestreamtv.tv.ui.browse.BrowseScreen
import com.cinestreamtv.tv.ui.detail.DetailScreen
import com.cinestreamtv.tv.ui.player.PlayerScreen
import com.cinestreamtv.tv.ui.search.SearchScreen
import com.cinestreamtv.tv.ui.settings.SettingsScreen
import com.cinestreamtv.tv.ui.extensions.ExtensionScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Browse : Screen("browse/{providerName}") {
        fun createRoute(providerName: String) = "browse/$providerName"
    }
    data object Detail : Screen("detail/{providerName}/{url}") {
        fun createRoute(providerName: String, url: String) = 
            "detail/$providerName/${java.net.URLEncoder.encode(url, "UTF-8")}"
    }
    data object Player : Screen("player/{providerName}/{data}/{title}/{mediaId}") {
        fun createRoute(providerName: String, data: String, title: String, mediaId: String) =
            "player/$providerName/${java.net.URLEncoder.encode(data, "UTF-8")}/${java.net.URLEncoder.encode(title, "UTF-8")}/$mediaId"
    }
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Extensions : Screen("extensions")
}

@Composable
fun CineStreamNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMediaClick = { item ->
                    navController.navigate(Screen.Detail.createRoute(item.providerName, item.url))
                },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onExtensionsClick = { navController.navigate(Screen.Extensions.route) }
            )
        }

        composable(
            route = Screen.Browse.route,
            arguments = listOf(navArgument("providerName") { type = NavType.StringType })
        ) { backStackEntry ->
            val providerName = backStackEntry.arguments?.getString("providerName") ?: ""
            BrowseScreen(
                providerName = providerName,
                onMediaClick = { item ->
                    navController.navigate(Screen.Detail.createRoute(item.providerName, item.url))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("providerName") { type = NavType.StringType },
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val providerName = backStackEntry.arguments?.getString("providerName") ?: ""
            val url = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("url") ?: "", "UTF-8"
            )
            DetailScreen(
                providerName = providerName,
                url = url,
                onPlayClick = { data, title, mediaId ->
                    navController.navigate(
                        Screen.Player.createRoute(providerName, data, title, mediaId)
                    )
                },
                onMediaClick = { item ->
                    navController.navigate(Screen.Detail.createRoute(item.providerName, item.url))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("providerName") { type = NavType.StringType },
                navArgument("data") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("mediaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val providerName = backStackEntry.arguments?.getString("providerName") ?: ""
            val data = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("data") ?: "", "UTF-8"
            )
            val title = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("title") ?: "", "UTF-8"
            )
            val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
            PlayerScreen(
                providerName = providerName,
                data = data,
                title = title,
                mediaId = mediaId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onMediaClick = { item ->
                    navController.navigate(Screen.Detail.createRoute(item.providerName, item.url))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onExtensionsClick = { navController.navigate(Screen.Extensions.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Extensions.route) {
            ExtensionScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
