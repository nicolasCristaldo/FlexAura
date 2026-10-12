package com.flexaura.features.auth.navigation

import kotlinx.serialization.Serializable

@Serializable object AuthGraph
@Serializable data class LoginRouteDestination(val email: String? = null)
@Serializable object RegisterRouteDestination