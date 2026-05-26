package com.flexaura.data.mapper

import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.data.domain.model.Post

fun PostWithInteractionsDto.toEntity(): PostEntity {
    val totalAura = post.votes1 + (post.votes3 * 3) + (post.votes5 * 5) - post.votesMinus1
    return PostEntity(
        id = post.id,
        authorId = post.authorId,
        content = post.content,
        imageUrl = post.imageUrl,
        createdAt = post.createdAt,
        commentsCount = post.commentsCount,
        savesCount = post.savesCount,
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