package app.eventbuzz.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.eventbuzz.feature.auth.AuthScreen
import app.eventbuzz.feature.detail.DetailScreen
import app.eventbuzz.feature.list.ListScreen
import app.eventbuzz.feature.map.MapScreen
import app.eventbuzz.feature.profile.ProfileScreen
import app.eventbuzz.feature.search.SearchScreen

@Composable
fun EventBuzzNavHost(
    isDarkMode: Boolean = false,
    onDarkModeChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelDestinations = TopLevelDestination.entries
    val topLevelRoutes = topLevelDestinations.map { it.route }

    val showBottomBar = currentDestination?.route in topLevelRoutes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label,
                                )
                            },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.MAP.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TopLevelDestination.MAP.route) {
                MapScreen(
                    onEventClick = { eventId ->
                        navController.navigate("event_detail/$eventId")
                    },
                )
            }

            composable(TopLevelDestination.LIST.route) {
                ListScreen(
                    onEventClick = { eventId ->
                        navController.navigate("event_detail/$eventId")
                    },
                )
            }

            composable(TopLevelDestination.SEARCH.route) {
                SearchScreen(
                    onEventClick = { eventId ->
                        navController.navigate("event_detail/$eventId")
                    },
                )
            }

            composable(TopLevelDestination.PROFILE.route) {
                ProfileScreen(
                    isDarkMode = isDarkMode,
                    onDarkModeChanged = onDarkModeChanged,
                    onNavigateToAuth = {
                        navController.navigate("auth")
                    },
                )
            }

            composable(
                route = "event_detail/{eventId}",
                arguments = listOf(
                    navArgument("eventId") { type = NavType.StringType }
                ),
            ) {
                DetailScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable("auth") {
                AuthScreen(
                    onAuthSuccess = { navController.popBackStack() },
                )
            }
        }
    }
}
