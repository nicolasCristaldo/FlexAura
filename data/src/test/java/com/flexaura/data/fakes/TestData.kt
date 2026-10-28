package com.flexaura.data.fakes

import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.UserDto
import java.util.UUID

object TestData {
    val defaultUser = UserDto(
        id = "user_1",
        username = "default",
        displayName = "Default",
        createdAt = "2024-01-01T00:00:00Z",
        totalAura = 100
    )

    val defaultPost = PostDto(
        id = "post_1",
        authorId = "user_1",
        content = "Content",
        createdAt = "2024-01-01T10:00:00Z"
    )

    val defaultComment = CommentDto(
        id = "comment_1",
        postId = "post_1",
        authorId = "user_1",
        content = "Content",
        createdAt = "2024-01-01T10:00:00Z"
    )

    fun createCommentDto(
        id: String = UUID.randomUUID().toString(),
        postId: String = "post_1",
        authorId: String = "user_1",
        content: String = "Content",
        createdAt: String = "2024-01-01T10:00:00Z"
    ) = CommentDto(
        id = id,
        postId = postId,
        authorId = authorId,
        content = content,
        createdAt = createdAt
    )

    fun createPostDto(
        id: String = UUID.randomUUID().toString(),
        authorId: String = "user_1",
        content: String? = null,
        imageUrl: String? = null,
        createdAt: String = "2024-01-01T10:00:00Z"
    ) = PostDto(
        id = id,
        authorId = authorId,
        content = content,
        imageUrl = imageUrl,
        createdAt = createdAt
    )

    fun createUserDto(
        id: String = UUID.randomUUID().toString(),
        username: String = "created user",
        displayName: String = "Created",
        createdAt: String = "2024-01-01T00:00:00Z"
    ) {
        UserDto(
            id = id,
            username = username,
            displayName = displayName,
            createdAt = createdAt
        )
    }
}