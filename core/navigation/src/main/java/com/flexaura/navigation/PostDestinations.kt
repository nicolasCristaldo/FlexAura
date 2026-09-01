package com.flexaura.navigation

import kotlinx.serialization.Serializable

@Serializable data class PostDetailRouteDestination(val postId: String)
@Serializable object PostDetailGraph
@Serializable object CreatePostRouteDestination