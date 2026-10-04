package com.flexaura.features.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.features.profile.editprofile.EditProfileRoute
import com.flexaura.features.profile.profilescreen.ProfileRoute

fun NavController.navigateToOwnProfile(builder: NavOptionsBuilder.() -> Unit) {
    navigate(route = ProfileGraph, builder = builder)
}

fun NavController.navigateToOtherUserProfile(userId: String, navOptions: NavOptions? = null) {
    navigate(route = OtherUserProfileRouteDestination(userId), navOptions = navOptions)
}

fun NavController.navigateToEditProfile(navOptions: NavOptions? = null) {
    navigate(route = ProfileDestinations.EditProfile, navOptions = navOptions)
}

fun NavGraphBuilder.profileGraph(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    navigation<ProfileGraph>(startDestination = ProfileDestinations.ProfileScreen()) {
        composable<ProfileDestinations.ProfileScreen> {
            ProfileRoute(
                onNavigateToPostDetail = onNavigateToPostDetail,
                onNavigateToCreatePost = onNavigateToCreatePost,
                onNavigateToAuthorProfile = onNavigateToAuthorProfile,
                onNavigateToEditProfile = onNavigateToEditProfile,
                onNavigateBack = onNavigateBack
            )
        }

        composable<ProfileDestinations.EditProfile> {
            EditProfileRoute(
                onNavigateBack = onNavigateBack
            )
        }
    }
}

fun NavGraphBuilder.otherUserProfileScreen(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    composable<OtherUserProfileRouteDestination> {
        ProfileRoute(
            onNavigateToPostDetail = onNavigateToPostDetail,
            onNavigateToCreatePost = onNavigateToCreatePost,
            onNavigateToAuthorProfile = onNavigateToAuthorProfile,
            onNavigateBack = onNavigateBack
        )
    }
}