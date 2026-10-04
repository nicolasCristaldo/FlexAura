package com.nicolascristaldo.flexaura.navigation

import com.flexaura.core.designsystem.R
import com.flexaura.features.home.navigation.HomeGraph
import com.flexaura.features.profile.navigation.ProfileGraph
import com.flexaura.search.navigation.SearchGraph

internal sealed class TopLevelDestination<T: Any>(val route: T, val iconRes: Int, val label: String) {
    object Home: TopLevelDestination<HomeGraph>(HomeGraph, R.drawable.ic_home, "Home")
    object Profile: TopLevelDestination<ProfileGraph>(ProfileGraph, R.drawable.ic_profile, "Profile")
    object Search: TopLevelDestination<SearchGraph>(SearchGraph, R.drawable.ic_search, "Search")
}

internal fun provideTopLevelDestinations() = listOf(
    TopLevelDestination.Home,
    TopLevelDestination.Search,
    TopLevelDestination.Profile
)