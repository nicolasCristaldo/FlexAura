package com.nicolascristaldo.flexaura.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.flexaura.navigation.HomeGraph
import com.flexaura.navigation.ProfileGraph
import com.flexaura.navigation.SearchGraph

sealed class TopLevelDestination<T: Any>(val route: T, val icon: ImageVector, val label: String) {
    object Home: TopLevelDestination<HomeGraph>(HomeGraph, Icons.Filled.Home, "Home")
    object Profile: TopLevelDestination<ProfileGraph>(ProfileGraph, Icons.Filled.AccountCircle, "Profile")
    object Search: TopLevelDestination<SearchGraph>(SearchGraph, Icons.Filled.Search, "Search")
}

internal fun provideTopLevelDestinations() = listOf(
    TopLevelDestination.Home,
    TopLevelDestination.Profile,
    TopLevelDestination.Search
)