package com.flexaura.data.mapper

import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.database.model.PostWithAuthorRelation
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.data.domain.model.Post

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