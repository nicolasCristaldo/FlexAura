package com.nicolascristaldo.flexaura.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.flexaura.createpost.navigation.createPostScreen
import com.flexaura.createpost.navigation.navigateToCreatePost
import com.flexaura.features.auth.navigation.AuthGraph
import com.flexaura.features.auth.navigation.RegisterRouteDestination
import com.flexaura.features.auth.navigation.authGraph
import com.flexaura.features.auth.navigation.navigateToLogin
import com.flexaura.features.auth.navigation.navigateToRegister
import com.flexaura.features.home.navigation.HomeGraph
import com.flexaura.features.home.navigation.HomeRouteDestination
import com.flexaura.features.home.navigation.homeGraph
import com.flexaura.features.home.navigation.navigateToHomeGraph
import com.flexaura.features.profile.navigation.ProfileDestinations
import com.flexaura.features.profile.navigation.navigateToEditProfile
import com.flexaura.features.profile.navigation.navigateToOwnProfile
import com.flexaura.features.profile.navigation.navigateToOtherUserProfile
import com.flexaura.features.profile.navigation.otherUserProfileScreen
import com.flexaura.features.profile.navigation.profileGraph
import com.flexaura.post.navigation.navigateToPostDetail
import com.flexaura.post.navigation.postDetailGraph
import com.flexaura.search.navigation.SearchGraph
import com.flexaura.search.navigation.SearchRouteDestination
import com.flexaura.search.navigation.navigateToSearchGraph
import com.flexaura.search.navigation.searchGraph

@Composable
fun FlexAuraNavHost(
    navController: NavHostController,
    startDestination: Any,
    currentUserId: String?
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val destinations = provideTopLevelDestinations()

    val showBottomBar = currentDestination?.let { destination ->
        val isHome = destination.hasRoute(HomeRouteDestination::class) ||
                destination.hasRoute(HomeGraph::class)
        val isSearch = destination.hasRoute(SearchRouteDestination::class) ||
                destination.hasRoute(SearchGraph::class)

        val isOwnProfile = destination.hasRoute(ProfileDestinations.ProfileScreen::class) &&
                navBackStackEntry?.arguments?.getString("userId") == null

        isHome || isSearch || isOwnProfile
    } ?: false

    val onNavigateToAuthorProfile: (String) -> Unit = { authorId ->
        if(currentUserId == authorId) {
            navController.navigateToOwnProfile {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
        else { navController.navigateToOtherUserProfile(authorId) }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FlexAuraBottomBar(
                    destinations = destinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = { destination ->
                        val navOptions: NavOptionsBuilder.() -> Unit = {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }

                        when(destination) {
                            TopLevelDestination.Profile -> navController.navigateToOwnProfile(navOptions)
                            TopLevelDestination.Search -> navController.navigateToSearchGraph(navOptions)
                            else -> navController.navigateToHomeGraph(navOptions)
                        }
                    },
                    modifier = Modifier.height(100.dp)
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            authGraph(
                onNavigateToLogin = {
                    navController.navigateToLogin {
                        popUpTo(RegisterRouteDestination) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigateToRegister() },
                onNavigateToHome = {
                    navController.navigateToHomeGraph {
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                }
            )

            homeGraph(
                onNavigateToPostDetail = { postId -> navController.navigateToPostDetail(postId) },
                onNavigateToCreatePost = { navController.navigateToCreatePost() },
                onNavigateToAuthorProfile = onNavigateToAuthorProfile
            )

            profileGraph(
                onNavigateToPostDetail = { postId -> navController.navigateToPostDetail(postId) },
                onNavigateToCreatePost = { navController.navigateToCreatePost() },
                onNavigateToAuthorProfile = onNavigateToAuthorProfile,
                onNavigateToEditProfile = { navController.navigateToEditProfile() },
                onNavigateBack = { navController.popBackStack() }
            )

            otherUserProfileScreen(
                onNavigateToPostDetail = { postId -> navController.navigateToPostDetail(postId) },
                onNavigateToCreatePost = { navController.navigateToCreatePost() },
                onNavigateToAuthorProfile = onNavigateToAuthorProfile,
                onNavigateBack = { navController.popBackStack() }
            )

            searchGraph(
                onNavigateToPostDetail = { postId -> navController.navigateToPostDetail(postId) },
                onNavigateToCreatePost = { navController.navigateToCreatePost() },
                onNavigateToAuthorProfile = onNavigateToAuthorProfile
            )

            postDetailGraph(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAuthorProfile = onNavigateToAuthorProfile
            )

            createPostScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}