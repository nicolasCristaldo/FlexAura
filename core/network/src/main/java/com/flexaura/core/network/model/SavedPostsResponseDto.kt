package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) wrapping a saved post returned in bookmark queries.
 *
 * @property post The associated [PostWithInteractionsDto] data.
 */
@Serializable
data class SavedPostsResponseDto(
    @SerialName("posts") val post: PostWithInteractionsDto
)