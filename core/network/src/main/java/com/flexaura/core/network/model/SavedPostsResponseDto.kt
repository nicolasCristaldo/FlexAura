package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedPostsResponseDto(
    @SerialName("posts") val post: PostWithInteractionsDto
)