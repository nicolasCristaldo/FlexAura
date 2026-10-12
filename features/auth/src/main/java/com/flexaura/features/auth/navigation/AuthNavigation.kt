package com.flexaura.features.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.flexaura.features.auth.login.LoginRoute
import com.flexaura.features.auth.register.RegisterRoute

fun NavController.navigateToAuthGraph(navOptionsBuilder: NavOptionsBuilder.() -> Unit) {
    navigate(route = AuthGraph, builder = navOptionsBuilder)
}

fun NavController.navigateToLogin(email: String? = null, builder: NavOptionsBuilder.() -> Unit) {
    navigate(route = LoginRouteDestination(email = email), builder = builder)
}

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    navigate(route = RegisterRouteDestination, navOptions = navOptions)
}

fun NavGraphBuilder.authGraph(
    onNavigateToLogin: (String?) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    navigation<AuthGraph>(startDestination = LoginRouteDestination()) {
        composable<LoginRouteDestination> {
            LoginRoute(
                onNavigateToRegister = onNavigateToRegister,
                onNavigateToHome = onNavigateToHome
            )
        }

        composable<RegisterRouteDestination> {
            RegisterRoute(
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}