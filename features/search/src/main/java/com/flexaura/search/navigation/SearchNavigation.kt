package com.flexaura.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.navigation.SearchGraph
import com.flexaura.navigation.SearchRouteDestination
import com.flexaura.search.SearchRoute

fun NavController.navigateToSearchGraph(navOptions: NavOptions? = null) {
    this.navigate(route = SearchGraph, navOptions = navOptions)
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