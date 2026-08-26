package com.flexaura.features.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.features.profile.ProfileRoute
import com.flexaura.navigation.ProfileGraph
import com.flexaura.navigation.ProfileRouteDestination

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileGraph, navOptions = navOptions)
}

fun NavGraphBuilder.profileScreen(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    composable<ProfileRouteDestination> {
        ProfileRoute(
            onNavigateToPostDetail = onNavigateToPostDetail,
            onNavigateToCreatePost = onNavigateToCreatePost,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavGraphBuilder.profileGraph(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit
) {
    navigation<ProfileGraph>(startDestination = ProfileRouteDestination()) {
        composable<ProfileRouteDestination> {
            ProfileRoute(
                onNavigateToPostDetail = onNavigateToPostDetail,
                onNavigateToCreatePost = onNavigateToCreatePost
            )
        }
    }
}