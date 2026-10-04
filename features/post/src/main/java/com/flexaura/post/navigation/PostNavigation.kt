package com.flexaura.post.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.flexaura.post.PostRoute

fun NavController.navigateToPostDetail(postId: String, navOptions: NavOptions? = null) {
    navigate(route = PostDetailRouteDestination(postId), navOptions = navOptions)
}

fun NavGraphBuilder.postDetailGraph(
    onNavigateBack: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit
) {
    composable<PostDetailRouteDestination> { backStackEntry ->
        val args = backStackEntry.toRoute<PostDetailRouteDestination>()
        PostRoute(
            postId = args.postId,
            onNavigateBack = onNavigateBack,
            onNavigateToAuthorProfile = onNavigateToAuthorProfile
        )
    }
}