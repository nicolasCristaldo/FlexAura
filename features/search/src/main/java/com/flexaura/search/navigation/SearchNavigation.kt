package com.flexaura.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.search.SearchRoute
import kotlinx.serialization.Serializable

@Serializable object SearchGraph
@Serializable object SearchRouteDestination

fun NavController.navigateToSearchGraph(navOptions: NavOptions? = null) {
    this.navigate(route = SearchGraph, navOptions = navOptions)
}

fun NavGraphBuilder.searchGraph() {
    navigation<SearchGraph>(startDestination = SearchRouteDestination) {
        composable<SearchRouteDestination> {
            SearchRoute()
        }
    }
}