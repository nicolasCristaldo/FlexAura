package com.flexaura.features.profile.navigation

import kotlinx.serialization.Serializable

@Serializable object ProfileGraph

@Serializable data class OtherUserProfileRouteDestination(val userId: String)

sealed interface ProfileDestinations {
    @Serializable
    data class ProfileScreen(val userId: String? = null) : ProfileDestinations

    @Serializable
    data object EditProfile : ProfileDestinations
}