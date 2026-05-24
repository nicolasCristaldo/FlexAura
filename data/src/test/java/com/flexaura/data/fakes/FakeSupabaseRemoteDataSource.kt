package com.flexaura.data.fakes

import com.flexaura.core.network.datasource.SupabaseRemoteDataSource
import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.FollowDto
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.core.network.model.SaveDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto
import com.flexaura.core.network.model.UserSaveIdDto
import com.flexaura.core.network.model.VoteDto

class FakeSupabaseRemoteDataSource : SupabaseRemoteDataSource {
    val remoteComments = mutableListOf<CommentDto>()
    val remoteUsers = mutableMapOf<String, UserDto>()
    val remotePosts = mutableListOf<PostWithInteractionsDto>()
    val remoteSaves = mutableListOf<SaveDto>()
    val remoteFollows = mutableListOf<FollowDto>()
    val remoteVotes = mutableListOf<VoteDto>()

    override val currentUserId: String?
        get() = "current_user"

    override suspend fun getUser(userId: String): UserDto? =
        remoteUsers[userId]

    override suspend fun updateUser(user: UserDto): UserDto {
        remoteUsers[user.id] = user
        return user
    }

    override suspend fun getUserProfile(userId: String): UserProfileDto? {
        val user = remoteUsers[userId] ?: return null
        return UserProfileDto(user = user)
    }

    override suspend fun followUser(followingId: String) {
        remoteUsers[followingId] = remoteUsers[followingId]?.copy(
            followersCount = (remoteUsers[followingId]?.followersCount ?: 0) + 1
        ) ?: remoteUsers[followingId]!!

        remoteUsers[currentUserId!!] = remoteUsers[currentUserId!!]?.copy(
            followingCount = (remoteUsers[currentUserId!!]?.followingCount ?: 0) + 1
        ) ?: remoteUsers[currentUserId!!]!!

        remoteFollows.add(
            FollowDto(
                followerId = currentUserId!!,
                followingId = followingId
            )
        )
    }

    override suspend fun unfollowUser(followingId: String) {
        remoteUsers[followingId] = remoteUsers[followingId]?.copy(
            followersCount = (remoteUsers[followingId]?.followersCount ?: 0) - 1
        ) ?: remoteUsers[followingId]!!

        remoteUsers[currentUserId!!] = remoteUsers[currentUserId!!]?.copy(
            followingCount = (remoteUsers[currentUserId!!]?.followingCount ?: 0) - 1
        ) ?: remoteUsers[currentUserId!!]!!

        remoteFollows.removeAll { it.followerId == currentUserId!! && it.followingId == followingId }
    }

    override suspend fun getFollowers(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val follows = remoteFollows.filter { it.followingId == userId }
        return follows.mapNotNull { remoteUsers[it.followerId] }
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val follows = remoteFollows.filter { it.followerId == userId }
        return follows.mapNotNull { remoteUsers[it.followingId] }
    }

    override suspend fun getPost(postId: String): PostDto? =
        remotePosts.map { it.post }.find { it.id == postId }

    override suspend fun getPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>
        = remotePosts

    override suspend fun createPost(post: PostDto): PostDto {
        remotePosts.add(PostWithInteractionsDto(post))
        return post
    }

    override suspend fun deletePost(postId: String) {
        remotePosts.removeAll { it.post.id == postId }
    }

    override suspend fun upsertVote(vote: VoteDto) {
        val index = remoteVotes.indexOfFirst { it.userId == vote.userId && it.postId == vote.postId }
        if (index != -1) remoteVotes[index] = vote
        else remoteVotes.add(vote)
    }

    override suspend fun deleteVote(postId: String) {
        remoteVotes.removeAll { it.userId == currentUserId!! && it.postId == postId }
    }

    override suspend fun getComments(postId: String): List<CommentDto> =
        remoteComments.filter { it.postId == postId }

    override suspend fun createComment(comment: CommentDto): CommentDto {
        remoteComments.add(comment)
        return comment
    }

    override suspend fun deleteComment(commentId: String) {
        remoteComments.removeAll { it.id == commentId }
    }

    override suspend fun savePost(save: SaveDto) {
        val index = remotePosts.indexOfFirst { it.post.id == save.postId }
        if (index != -1) {
            val currentPost = remotePosts[index]
            val updatedUserSave = currentPost.userSave + UserSaveIdDto(id = save.userId)
            remotePosts[index] = currentPost.copy(userSave = updatedUserSave)
        }
        remoteSaves.add(save)
    }

    override suspend fun unsavePost(postId: String) {
        val index = remotePosts.indexOfFirst { it.post.id == postId }
        if (index != -1) {
            val currentPost = remotePosts[index]
            val updatedUserSave = currentPost.userSave.filter { it.id != currentUserId!! }
            remotePosts[index] = currentPost.copy(userSave = updatedUserSave)
        }
        remoteSaves.removeAll { it.userId == currentUserId!! && it.postId == postId }
    }

    override suspend fun getSavedPosts(
        page: Int,
        pageSize: Int
    ): List<PostDto> = remotePosts.filter {
        it.userSave.any { userSave -> userSave.id == currentUserId!! }
    }.map { it.post }
}