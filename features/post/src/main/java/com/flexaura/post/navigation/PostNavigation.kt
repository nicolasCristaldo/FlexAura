package com.flexaura.post.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.flexaura.navigation.PostDetailRouteDestination
import com.flexaura.post.PostRoute

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