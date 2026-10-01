package com.flexaura.navigation

import kotlinx.serialization.Serializable

@Serializable object ProfileGraph

@Serializable data class UserProfileRouteDestination(val userId: String)

sealed interface ProfileDestinations {
    @Serializable
    data class ProfileScreen(val userId: String? = null) : ProfileDestinations

    @Serializable
    data object EditProfile : ProfileDestinations
}