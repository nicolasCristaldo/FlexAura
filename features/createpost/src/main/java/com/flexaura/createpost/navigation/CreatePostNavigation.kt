package com.flexaura.createpost.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.flexaura.createpost.CreatePostRoute
import com.flexaura.navigation.CreatePostRouteDestination

fun NavGraphBuilder.createPostScreen(
    onNavigateBack: () -> Unit
) {
    composable<CreatePostRouteDestination> {
        CreatePostRoute(onNavigateBack = onNavigateBack)
    }
}