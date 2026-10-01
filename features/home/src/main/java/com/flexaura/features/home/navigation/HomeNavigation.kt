package com.flexaura.features.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.features.home.HomeRoute

fun NavController.navigateToHomeGraph(builder: NavOptionsBuilder.() -> Unit) {
    navigate(route = HomeGraph, builder = builder)
}

fun NavGraphBuilder.homeGraph(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit
) {
    navigation<HomeGraph>(startDestination = HomeRouteDestination) {
        composable<HomeRouteDestination> {
            HomeRoute(
                onNavigateToPostDetail = onNavigateToPostDetail,
                onNavigateToCreatePost = onNavigateToCreatePost,
                onNavigateToAuthorProfile = onNavigateToAuthorProfile
            )
        }
    }
}
