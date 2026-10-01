package com.flexaura.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.search.SearchRoute

fun NavController.navigateToSearchGraph(builder: NavOptionsBuilder.() -> Unit) {
    navigate(route = SearchGraph, builder = builder)
}

fun NavGraphBuilder.searchGraph(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit
) {
    navigation<SearchGraph>(startDestination = SearchRouteDestination) {
        composable<SearchRouteDestination> {
            SearchRoute(
                onNavigateToPostDetail = onNavigateToPostDetail,
                onNavigateToCreatePost = onNavigateToCreatePost,
                onNavigateToAuthorProfile = onNavigateToAuthorProfile
            )
        }
    }
}