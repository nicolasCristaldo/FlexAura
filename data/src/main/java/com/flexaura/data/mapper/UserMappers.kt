package com.flexaura.data.mapper

import com.flexaura.core.database.model.UserEntity
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto
import com.flexaura.data.domain.model.User

/**
 * Converts a [UserProfileDto] into a [UserEntity] for local storage.
 *
 * @receiver The user profile DTO to convert.
 * @return The corresponding [UserEntity].
 */
fun UserProfileDto.toEntity(): UserEntity = UserEntity(
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

/**
 * Converts a [UserEntity] from the local database into a domain [User] model.
 *
 * @receiver The user entity to convert.
 * @return The corresponding domain [User].
 */
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

/**
 * Converts a [UserDto] into a [UserEntity] for local storage with an optional follow flag.
 *
 * @receiver The user DTO to convert.
 * @param isCurrentUserFollowing Flag indicating whether the current user follows this profile. Defaults to checking the follows list.
 * @return The corresponding [UserEntity].
 */
fun UserDto.toEntity(
    isCurrentUserFollowing: Boolean = follows.isNotEmpty()
): UserEntity = UserEntity(
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