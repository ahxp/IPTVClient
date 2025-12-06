package com.ahxp.iptvclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ahxp.iptvclient.data.local.ProfileManager
import com.ahxp.iptvclient.ui.channels.ChannelListScreen
import com.ahxp.iptvclient.ui.dashboard.DashboardScreen
import com.ahxp.iptvclient.ui.home.HomeScreen
import com.ahxp.iptvclient.ui.login.LoginScreen
import com.ahxp.iptvclient.ui.movies.MovieListScreen
import com.ahxp.iptvclient.ui.movies.MoviesScreen
import com.ahxp.iptvclient.ui.player.PlayerScreen
import com.ahxp.iptvclient.ui.profiles.ProfilesScreen
import com.ahxp.iptvclient.ui.series.SeriesDetailScreen
import com.ahxp.iptvclient.ui.series.SeriesListScreen
import com.ahxp.iptvclient.ui.series.SeriesScreen
import com.ahxp.iptvclient.ui.theme.IPTVClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val profileManager = ProfileManager(this)
        // Determine start destination
        val startDestination = if (profileManager.getActiveProfile() != null) {
            "dashboard"
        } else if (profileManager.getAllProfiles().isNotEmpty()) {
            "profiles"
        } else {
            "login"
        }

        setContent {
            IPTVClientTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("profiles") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("profiles") {
                            ProfilesScreen(
                                onProfileSelected = {
                                    navController.navigate("dashboard") {
                                        popUpTo("profiles") { inclusive = true }
                                    }
                                },
                                onAddProfileClick = {
                                    navController.navigate("login")
                                }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                onLiveTvClick = { navController.navigate("home") },
                                onMoviesClick = { navController.navigate("movies") },
                                onSeriesClick = { navController.navigate("series") },
                                onSwitchProfileClick = {
                                    profileManager.clearActiveProfile()
                                    navController.navigate("profiles") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }
                        // --- Live TV ---
                        composable("home") {
                            HomeScreen(
                                onCategoryClick = { categoryId ->
                                    navController.navigate("channels/$categoryId")
                                },
                                onLogoutClick = {
                                    navController.navigate("dashboard") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
                            route = "channels/{categoryId}",
                            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                            ChannelListScreen(
                                categoryId = categoryId,
                                onChannelClick = { streamId ->
                                    navController.navigate("player/$streamId?type=live")
                                }
                            )
                        }
                        // --- Movies ---
                        composable("movies") {
                            MoviesScreen(
                                onCategoryClick = { categoryId ->
                                    navController.navigate("movies_list/$categoryId")
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "movies_list/{categoryId}",
                            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                            MovieListScreen(
                                categoryId = categoryId,
                                onMovieClick = { streamId, ext ->
                                    navController.navigate("player/$streamId?type=movie&ext=$ext")
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        // --- Series ---
                        composable("series") {
                            SeriesScreen(
                                onCategoryClick = { categoryId ->
                                    navController.navigate("series_list/$categoryId")
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "series_list/{categoryId}",
                            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                            SeriesListScreen(
                                categoryId = categoryId,
                                onSeriesClick = { seriesId ->
                                    navController.navigate("series_detail/$seriesId")
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "series_detail/{seriesId}",
                            arguments = listOf(navArgument("seriesId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val seriesId = backStackEntry.arguments?.getInt("seriesId") ?: 0
                            SeriesDetailScreen(
                                seriesId = seriesId,
                                onEpisodeClick = { streamId, ext ->
                                    navController.navigate("player/$streamId?type=series&ext=$ext")
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // --- Player ---
                        composable(
                            route = "player/{streamId}?type={type}&ext={ext}",
                            arguments = listOf(
                                navArgument("streamId") { type = NavType.StringType },
                                navArgument("type") { defaultValue = "live" },
                                navArgument("ext") { defaultValue = "" }
                            )
                        ) { backStackEntry ->
                            val streamId = backStackEntry.arguments?.getString("streamId") ?: ""
                            val type = backStackEntry.arguments?.getString("type") ?: "live"
                            val ext = backStackEntry.arguments?.getString("ext") ?: ""
                            PlayerScreen(streamId = streamId, type = type, extension = ext)
                        }
                    }
                }
            }
        }
    }
}
