package com.flexaura.features.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.features.home.HomeRoute
import com.flexaura.navigation.HomeGraph
import com.flexaura.navigation.HomeRouteDestination

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    this.navigate(route = HomeGraph, navOptions = navOptions)
}

fun NavGraphBuilder.homeGraph(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    nestedGraphs: (NavGraphBuilder.() -> Unit)? = null
) {
    navigation<HomeGraph>(startDestination = HomeRouteDestination) {
        composable<HomeRouteDestination> {
            HomeRoute(
                onNavigateToPostDetail = onNavigateToPostDetail,
                onNavigateToCreatePost = onNavigateToCreatePost,
                onNavigateToAuthorProfile = onNavigateToAuthorProfile
            )
        }

        nestedGraphs?.invoke(this)
    }
}
