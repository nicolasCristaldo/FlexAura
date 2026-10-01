package com.flexaura.features.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flexaura.features.profile.editprofile.EditProfileRoute
import com.flexaura.features.profile.profilescreen.ProfileRoute
import com.flexaura.navigation.ProfileDestinations
import com.flexaura.navigation.ProfileGraph

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileGraph, navOptions = navOptions)
}

fun NavGraphBuilder.profileGraph(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: (() -> Unit)? = null
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