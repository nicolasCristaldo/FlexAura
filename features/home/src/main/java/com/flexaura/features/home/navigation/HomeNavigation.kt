package com.flexaura.features.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.features.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable object HomeGraph
@Serializable object HomeRouteDestination

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    this.navigate(route = HomeGraph, navOptions = navOptions)
}

fun NavGraphBuilder.homeGraph() {
    navigation<HomeGraph>(startDestination = HomeRouteDestination) {
        composable<HomeRouteDestination> {
            HomeRoute()
        }
    }
}
