package com.flexaura.data.mapper

import com.flexaura.core.database.model.CommentEntity
import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.CommentWithAuthorDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.data.domain.model.Comment

/**
 * Converts a [CommentDto] and its author [UserDto] into a [CommentEntity] for local database storage.
 *
 * @receiver The comment DTO to convert.
 * @param author The author's [UserDto] containing profile details.
 * @return The corresponding [CommentEntity].
 */
fun CommentDto.toEntity(author: UserDto): CommentEntity = CommentEntity(
    id = id,
    postId = postId,
    userId = authorId,
    content = content,
    createdAt = createdAt ?: "",
    authorUsername = author.username,
    authorDisplayName = author.displayName,
    authorAvatarUrl = author.imageUrl,
    authorTotalAura = author.totalAura
)

/**
 * Converts a [CommentEntity] from the local database into a domain [Comment] model.
 *
 * @receiver The comment entity to convert.
 * @return The corresponding domain [Comment].
 */
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

/**
 * Converts a [CommentWithAuthorDto] containing nested author details into a [CommentEntity].
 *
 * @receiver The comment with author DTO to convert.
 * @return The corresponding [CommentEntity].
 */
fun CommentWithAuthorDto.toEntity(): CommentEntity = CommentEntity(
    id = id,
    postId = postId,
    userId = authorId,
    content = content,
    createdAt = createdAt ?: "",
    authorUsername = author.username,
    authorDisplayName = author.displayName,
    authorAvatarUrl = author.imageUrl,
    authorTotalAura = author.totalAura
)