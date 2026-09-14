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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.flexaura.createpost.navigation.createPostScreen
import com.flexaura.features.auth.navigation.authGraph
import com.flexaura.features.home.navigation.homeGraph
import com.flexaura.features.profile.navigation.profileGraph
import com.flexaura.features.profile.navigation.profileScreen
import com.flexaura.navigation.AuthGraph
import com.flexaura.navigation.CreatePostRouteDestination
import com.flexaura.navigation.HomeGraph
import com.flexaura.navigation.HomeRouteDestination
import com.flexaura.navigation.LoginRouteDestination
import com.flexaura.navigation.PostDetailRouteDestination
import com.flexaura.navigation.ProfileGraph
import com.flexaura.navigation.ProfileRouteDestination
import com.flexaura.navigation.RegisterRouteDestination
import com.flexaura.navigation.SearchRouteDestination
import com.flexaura.post.navigation.postDetailGraph
import com.flexaura.search.navigation.searchGraph

@Composable
fun FlexAuraNavHost(
    navController: NavHostController,
    startDestination: Any
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val destinations = provideTopLevelDestinations()

    val showBottomBar = currentDestination?.let { destination ->
        destination.hasRoute(HomeRouteDestination::class) ||
        destination.hasRoute(SearchRouteDestination::class) ||
        (destination.hasRoute(ProfileRouteDestination::class) && destination.parent?.hasRoute(ProfileGraph::class) == true)
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FlexAuraBottomBar(
                    destinations = destinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = { destination ->
                        val route = when(destination) {
                            TopLevelDestination.Home -> HomeGraph
                            TopLevelDestination.Profile -> ProfileGraph
                            TopLevelDestination.Search -> SearchRouteDestination
                            else -> HomeGraph
                        }

                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
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

            homeGraph(
                onNavigateToPostDetail = { postId ->
                    navController.navigate(PostDetailRouteDestination(postId))
                },
                onNavigateToCreatePost = {
                    navController.navigate(CreatePostRouteDestination)
                },
                onNavigateToAuthorProfile = { authorId ->
                    navController.navigate(ProfileRouteDestination(authorId))
                },
                nestedGraphs = {
                    profileScreen(
                        onNavigateToPostDetail = { postId ->
                            navController.navigate(PostDetailRouteDestination(postId))
                        },
                        onNavigateToCreatePost = {
                            navController.navigate(CreatePostRouteDestination)
                        },
                        onNavigateBack = {
                             navController.popBackStack()
                        }
                    )
                }
            )

            profileGraph(
                onNavigateToPostDetail = { postId ->
                    navController.navigate(PostDetailRouteDestination(postId))
                },
                onNavigateToCreatePost = {
                    navController.navigate(CreatePostRouteDestination)
                }
            )

            searchGraph(
                onNavigateToPostDetail = { postId ->
                    navController.navigate(PostDetailRouteDestination(postId))
                },
                onNavigateToCreatePost = {
                    navController.navigate(CreatePostRouteDestination)
                },
                onNavigateToAuthorProfile = { authorId ->
                    navController.navigate(ProfileRouteDestination(authorId))
                },
                nestedGraphs = {
                    profileScreen(
                        onNavigateToPostDetail = { postId ->
                            navController.navigate(PostDetailRouteDestination(postId))
                        },
                        onNavigateToCreatePost = {
                            navController.navigate(CreatePostRouteDestination)
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            )

            postDetailGraph(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAuthorProfile = { authorId ->
                    navController.navigate(ProfileRouteDestination(authorId))
                },
                nestedGraphs = {
                    profileScreen(
                        onNavigateToPostDetail = { postId ->
                            navController.navigate(PostDetailRouteDestination(postId))
                        },
                        onNavigateToCreatePost = {
                            navController.navigate(CreatePostRouteDestination)
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            )

            createPostScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}