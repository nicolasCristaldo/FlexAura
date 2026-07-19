package com.flexaura.navigation

import kotlinx.serialization.Serializable

@Serializable object ProfileGraph
@Serializable data class ProfileRouteDestination(val userId: String? = null)