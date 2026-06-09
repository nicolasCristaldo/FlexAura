package com.nicolascristaldo.flexaura.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flexaura.features.auth.navigation.AuthGraph
import com.flexaura.features.auth.navigation.LoginRouteDestination
import com.flexaura.features.auth.navigation.RegisterRouteDestination
import com.flexaura.features.auth.navigation.authGraph
import com.flexaura.features.home.navigation.HomeGraph
import com.flexaura.features.home.navigation.homeGraph


@Composable
fun FlexAuraNavHost(
    navController: NavHostController,
    startDestination: Any,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            onNavigateToLogin = {
                navController.navigate(LoginRouteDestination) {
                    popUpTo(RegisterRouteDestination) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(RegisterRouteDestination)
            },
            onNavigateToHome = {
                navController.navigate(HomeGraph) {
                    popUpTo(AuthGraph) { inclusive = true }
                }
            }
        )

        homeGraph()
    }
}