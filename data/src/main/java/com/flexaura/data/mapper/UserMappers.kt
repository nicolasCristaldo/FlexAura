package com.flexaura.data.mapper

import com.flexaura.core.database.model.UserEntity
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto
import com.flexaura.data.domain.model.User

fun UserProfileDto.toEntity(): UserEntity = UserEntity(
    id = user.id,
    username = user.username,
    displayName = user.displayName,
    imageUrl = user.imageUrl,
    bio = user.bio,
    totalAura = user.totalAura,
    followersCount = user.followersCount,
    followingCount = user.followingCount,
    createdAt = user.createdAt,
    isFollowedByCurrentUser = isCurrentUserFollowing
)

fun UserEntity.toDomain(): User = User(
    id = id,
    username = username,
    displayName = displayName,
    imageUrl = imageUrl,
    bio = bio,
    totalAura = totalAura,
    followersCount = followersCount,
    followingCount = followingCount,
    isFollowedByCurrentUser = isFollowedByCurrentUser
)

fun UserDto.toEntity(isCurrentUserFollowing: Boolean = false): UserEntity = UserEntity(
    id = id,
    username = username,
    displayName = displayName,
    imageUrl = imageUrl,
    bio = bio,
    totalAura = totalAura,
    followersCount = followersCount,
    followingCount = followingCount,
    createdAt = createdAt,
    isFollowedByCurrentUser = isCurrentUserFollowing
)