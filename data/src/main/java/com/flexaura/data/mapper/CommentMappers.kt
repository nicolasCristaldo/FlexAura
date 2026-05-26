package com.flexaura.data.mapper

import com.flexaura.core.database.model.CommentEntity
import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.data.domain.model.Comment

fun CommentDto.toEntity(author: UserDto): CommentEntity = CommentEntity(
    id = id,
    postId = postId,
    userId = authorId,
    content = content,
    createdAt = createdAt,
    authorUsername = author.username,
    authorDisplayName = author.displayName,
    authorAvatarUrl = author.imageUrl,
    authorTotalAura = author.totalAura
)

fun CommentEntity.toDomain(): Comment = Comment(
    id = id,
    postId = postId,
    authorId = userId,
    content = content,
    createdAt = createdAt,
    authorUsername = authorUsername,
    authorDisplayName = authorDisplayName,
    authorAvatarUrl = authorAvatarUrl,
    authorTotalAura = authorTotalAura
)