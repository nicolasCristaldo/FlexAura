package com.flexaura.core.network.datasource.database.user

import com.flexaura.core.network.datasource.auth.UserSession
import com.flexaura.core.network.model.FollowDto
import com.flexaura.core.network.model.FollowResponseDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserFollowCheckDto
import com.flexaura.core.network.model.UserProfileDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val userSession: UserSession
): UserRemoteDataSource {
    // -------------------- Users --------------------
    override suspend fun getUser(userId: String): UserDto? =
        supabaseClient.postgrest["users"]
            .select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<UserDto>()

    override suspend fun updateProfile(
        username: String,
        displayName: String,
        imageUrl: String?,
        bio: String?
    ): UserDto {
        val currentId = userSession.requireCurrentUserId()
        val updateBody = buildJsonObject {
            put("username", username)
            put("display_name", displayName)
            put("image_url", imageUrl)
            put("bio", bio)
        }

        return supabaseClient.postgrest["users"]
            .update(updateBody) {
                filter { eq("id", currentId) }
                select()
            }.decodeSingle<UserDto>()
    }

    override suspend fun getUserProfile(userId: String): UserProfileDto? {
        val currentId = userSession.requireCurrentUserId()

        val userDto = supabaseClient.postgrest["users"]
            .select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<UserDto>() ?: return null

        val followResult = supabaseClient.postgrest["follows"]
            .select {
                filter {
                    eq("follower_id", currentId)
                    eq("following_id", userId)
                }
            }.decodeList<FollowDto>()

        val isFollowing = followResult.isNotEmpty()

        return UserProfileDto(
            id = userDto.id,
            username = userDto.username,
            displayName = userDto.displayName,
            imageUrl = userDto.imageUrl,
            bio = userDto.bio,
            totalAura = userDto.totalAura,
            followersCount = userDto.followersCount,
            followingCount = userDto.followingCount,
            createdAt = userDto.createdAt,
            isFollowingList = if (isFollowing) {
                listOf(UserFollowCheckDto(id = followResult.firstOrNull()?.id ?: "exists"))
            }
            else emptyList()
        )
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val searchPattern = "%$query%"
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["users"]
            .select {
                filter {
                    or {
                        ilike("username", searchPattern)
                        ilike("display_name", searchPattern)
                        ilike("bio", searchPattern)
                    }
                }
                range(from.toLong(), to.toLong())
            }.decodeList<UserDto>()
    }

    // -------------------- Follows --------------------
    override suspend fun followUser(followingId: String) {
        val currentId = userSession.requireCurrentUserId()
        val follow = FollowDto(followerId = currentId, followingId = followingId)
        supabaseClient.postgrest["follows"].insert(follow)
    }

    override suspend fun unfollowUser(followingId: String) {
        val currentId = userSession.requireCurrentUserId()
        supabaseClient.postgrest["follows"]
            .delete {
                filter {
                    eq("follower_id", currentId)
                    eq("following_id", followingId)
                }
            }
    }

    override suspend fun getFollowers(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["follows"]
            .select(columns = Columns.Companion.raw("users!follower_id(*)")) {
                filter { eq("following_id", userId) }
                range(from.toLong(), to.toLong())
            }.decodeList<FollowResponseDto>().map { it.user }
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["follows"]
            .select(columns = Columns.Companion.raw("users!following_id(*)")) {
                filter { eq("follower_id", userId) }
                range(from.toLong(), to.toLong())
            }.decodeList<FollowResponseDto>().map { it.user }
    }
}