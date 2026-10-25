package com.flexaura.data.mapper

import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.database.model.PostWithAuthorRelation
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.data.domain.model.Post

/**
 * Converts a [PostWithInteractionsDto] into a [PostEntity] for local storage, calculating the total aura score.
 *
 * @receiver The post with interactions DTO to convert.
 * @return The corresponding [PostEntity].
 */
fun PostWithInteractionsDto.toEntity(): PostEntity {
    val totalAura = votes1 + (votes3 * 3) + (votes5 * 5) - votesMinus1
    return PostEntity(
        id = id,
        authorId = authorId,
        content = content,
        imageUrl = imageUrl,
        createdAt = createdAt,
        commentsCount = commentsCount,
        savesCount = savesCount,
        totalAura = totalAura,
        currentUserVote = currentVoteValue,
        isSavedByUser = isSaved
    )
}

/**
 * Converts a [PostEntity] from the local database into a domain [Post] model.
 *
 * @receiver The post entity to convert.
 * @return The corresponding domain [Post].
 */
fun PostEntity.toDomain(): Post = Post(
    id = id,
    authorId = authorId,
    content = content,
    imageUrl = imageUrl,
    createdAt = createdAt,
    commentsCount = commentsCount,
    savesCount = savesCount,
    totalAura = totalAura,
    currentUserVote = currentUserVote,
    isSavedByUser = isSavedByUser
)

/**
 * Converts a raw [PostDto] into a [PostEntity] for local storage, computing total aura from vote weights.
 *
 * @receiver The post DTO to convert.
 * @return The corresponding [PostEntity].
 */
fun PostDto.toEntity(): PostEntity {
    val totalAura = votes1 + (votes3 * 3) + (votes5 * 5) - votesMinus1

    return PostEntity(
        id = id,
        authorId = authorId,
        content = content,
        imageUrl = imageUrl,
        createdAt = createdAt,
        commentsCount = commentsCount,
        savesCount = savesCount,
        totalAura = totalAura,
        currentUserVote = 0,
        isSavedByUser = false
    )
}

/**
 * Converts a [PostWithAuthorRelation] database query result into a domain [Post] model.
 *
 * @receiver The post with author relation to convert.
 * @return The corresponding domain [Post] including its author profile if available.
 */
fun PostWithAuthorRelation.toDomain(): Post = Post(
    id = post.id,
    authorId = post.authorId,
    content = post.content,
    imageUrl = post.imageUrl,
    createdAt = post.createdAt,
    commentsCount = post.commentsCount,
    savesCount = post.savesCount,
    totalAura = post.totalAura,
    currentUserVote = post.currentUserVote,
    isSavedByUser = post.isSavedByUser,
    author = author?.toDomain()
)