package com.flexaura.features.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.features.profile.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable object ProfileGraph
@Serializable data class ProfileRouteDestination(val userId: String? = null)

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileGraph, navOptions = navOptions)
}

fun NavGraphBuilder.profileGraph() {
    navigation<ProfileGraph>(startDestination = ProfileRouteDestination()) {
        composable<ProfileRouteDestination> {
            ProfileRoute()
        }
    }
}