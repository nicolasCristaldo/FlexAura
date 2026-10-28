package com.flexaura.data.fakes.datasource

import com.flexaura.core.network.datasource.database.post.PostRemoteDataSource
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.core.network.model.SaveDto
import com.flexaura.core.network.model.UserSaveIdDto
import com.flexaura.core.network.model.UserVoteValueDto
import com.flexaura.core.network.model.VoteDto
import com.flexaura.data.fakes.TestData
import com.flexaura.data.fakes.TestState
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import java.util.UUID

class FakePostRemoteDataSource(
    val state: TestState
) : PostRemoteDataSource {

    private fun mapToPostWithInteractions(posts: List<PostDto>): List<PostWithInteractionsDto> {
        val users = state.users.value
        val votes = state.votes.value
        val saves = state.saves.value
        val currentUserId = state.currentUserId

        return posts.map { post ->
            val userVote = votes
                .filter { it.postId == post.id && it.userId == currentUserId }
                .map { UserVoteValueDto(it.value) }
            val userSave = saves
                .filter { it.postId == post.id && it.userId == currentUserId }
                .map { UserSaveIdDto(it.id ?: UUID.randomUUID().toString()) }

            PostWithInteractionsDto(
                id = post.id,
                authorId = post.authorId,
                content = post.content,
                imageUrl = post.imageUrl,
                createdAt = post.createdAt,
                commentsCount = post.commentsCount,
                savesCount = post.savesCount,
                votes1 = post.votes1,
                votes3 = post.votes3,
                votes5 = post.votes5,
                votesMinus1 = post.votesMinus1,
                userVote = userVote,
                userSave = userSave,
                author = users.find { it.id == post.authorId }
            )
        }
    }


    override suspend fun getPost(postId: String): PostDto? =
        state.posts.value.find { it.id == postId }

    override suspend fun getPosts(
        page: Int,
        pageSize: Int
    ): List<PostWithInteractionsDto> {
        val posts = state.posts.value
            .sortedByDescending { it.createdAt }
            .drop((page - 1) * pageSize)
            .take(pageSize)

        return mapToPostWithInteractions(posts)
    }

    override suspend fun createPost(
        id: String,
        content: String?,
        imageUrl: String?
    ): PostDto {
        val newPost = TestData.createPostDto(
            id = id,
            content = content,
            imageUrl = imageUrl,
            createdAt = Clock.System.now().toString()
        )

        state.posts.update { it + newPost }

        return newPost
    }

    override suspend fun deletePost(postId: String) =
        state.posts.update { posts -> posts.filterNot { it.id == postId } }

    override suspend fun getPostsByAuthor(
        authorId: String,
        page: Int,
        pageSize: Int
    ): List<PostWithInteractionsDto> {
        val posts = state.posts.value
            .filter { it.authorId == authorId }
            .sortedByDescending { it.createdAt }
            .drop((page - 1) * pageSize)
            .take(pageSize)

        return mapToPostWithInteractions(posts)
    }

    override suspend fun searchPosts(
        query: String,
        page: Int,
        pageSize: Int
    ): List<PostWithInteractionsDto> {
        val posts = state.posts.value
            .filter { it.content?.contains(query, ignoreCase = true) == true }
            .sortedByDescending { it.createdAt }
            .drop((page - 1) * pageSize)
            .take(pageSize)

        return mapToPostWithInteractions(posts)
    }

    override suspend fun getFollowingPosts(
        page: Int,
        pageSize: Int
    ): List<PostWithInteractionsDto> {
        val followingIds = state.users.value
            .find { it.id == state.currentUserId }
            ?.follows
            ?.map { it.id } ?: emptyList()

        val posts = state.posts.value
            .filter { followingIds.contains(it.authorId) }
            .sortedByDescending { it.createdAt }
            .drop((page - 1) * pageSize)
            .take(pageSize)

        return mapToPostWithInteractions(posts)
    }

    override suspend fun upsertVote(postId: String, value: Int) =
        state.votes.update { votes ->
            val filteredVotes =
                votes.filter { it.postId != postId && it.userId == state.currentUserId }
            filteredVotes + VoteDto(
                id = UUID.randomUUID().toString(),
                userId = state.currentUserId,
                postId = postId,
                value = value
            )
        }

    override suspend fun deleteVote(postId: String) =
        state.votes.update { votes -> votes.filter { it.postId != postId && it.userId == state.currentUserId } }

    override suspend fun savePost(postId: String) {
        state.saves.value.any { it.postId == postId && it.userId == state.currentUserId }

        state.saves.update { saves ->
            saves + SaveDto(
                id = UUID.randomUUID().toString(),
                userId = state.currentUserId,
                postId = postId
            )
        }

        state.posts.update { posts ->
            posts.map { post ->
                if (post.id == postId) {
                    post.copy(savesCount = post.savesCount + 1)
                } else {
                    post
                }
            }
        }
    }

    override suspend fun unsavePost(postId: String) {
        state.saves.update { saves -> saves.filterNot { it.postId == postId && it.userId == state.currentUserId } }

        state.posts.update { posts ->
            posts.map { post ->
                if (post.id == postId) {
                    post.copy(savesCount = post.savesCount - 1)
                } else {
                    post
                }
            }
        }
    }

    override suspend fun getSavedPosts(
        page: Int,
        pageSize: Int
    ): List<PostWithInteractionsDto> {
        val savedIds = state.saves.value
            .filter { it.userId == state.currentUserId }
            .map { it.postId }

        val posts = state.posts.value
            .filter { savedIds.contains(it.id) }
            .sortedByDescending { it.createdAt }
            .drop((page - 1) * pageSize)
            .take(pageSize)

        return mapToPostWithInteractions(posts)
    }
}