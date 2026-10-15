package com.nicolascristaldo.flexaura.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flexaura.core.designsystem.R
import com.flexaura.features.home.navigation.HomeGraph
import com.flexaura.features.profile.navigation.ProfileGraph
import com.flexaura.search.navigation.SearchGraph

internal sealed class TopLevelDestination<T: Any>(
    val route: T,
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    object Home: TopLevelDestination<HomeGraph>(HomeGraph, R.drawable.ic_home, R.string.home)
    object Profile: TopLevelDestination<ProfileGraph>(ProfileGraph, R.drawable.ic_profile, R.string.profile)
    object Search: TopLevelDestination<SearchGraph>(SearchGraph, R.drawable.ic_search, R.string.search)
}

internal fun provideTopLevelDestinations() = listOf(
    TopLevelDestination.Home,
    TopLevelDestination.Search,
    TopLevelDestination.Profile
)