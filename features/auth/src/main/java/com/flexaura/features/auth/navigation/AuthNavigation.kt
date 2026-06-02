package com.flexaura.features.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.flexaura.features.auth.login.LoginRoute
import com.flexaura.features.auth.register.RegisterRoute
import kotlinx.serialization.Serializable

@Serializable object AuthGraph
@Serializable object LoginRouteDestination
@Serializable object RegisterRouteDestination

fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    this.navigate(route = AuthGraph, navOptions = navOptions)
}

fun NavGraphBuilder.authGraph(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    navigation<AuthGraph>(startDestination = LoginRouteDestination) {
        composable<LoginRouteDestination> {
            LoginRoute(
                onNavigateToRegister = onNavigateToRegister,
                onNavigateToHome = onNavigateToHome
            )
        }

        composable<RegisterRouteDestination> {
            RegisterRoute(
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}