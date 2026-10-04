package com.flexaura.createpost.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.flexaura.createpost.CreatePostRoute

fun NavController.navigateToCreatePost(navOptions: NavOptions? = null) {
    navigate(route = CreatePostRouteDestination, navOptions = navOptions)
}

fun NavGraphBuilder.createPostScreen(
    onNavigateBack: () -> Unit
) {
    composable<CreatePostRouteDestination> {
        CreatePostRoute(onNavigateBack = onNavigateBack)
    }
}