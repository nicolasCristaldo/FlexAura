package com.flexaura.data.fakes.dao

import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.database.model.PostWithAuthorRelation
import com.flexaura.core.database.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class FakePostDao(
    private val usersTable: MutableStateFlow<List<UserEntity>> = MutableStateFlow(emptyList())
) : PostDao {
    val postsTable = MutableStateFlow<List<PostEntity>>(emptyList())

    private fun mapListToRelation(posts: List<PostEntity>, users: List<UserEntity>): List<PostWithAuthorRelation> {
        return posts.map { post ->
            PostWithAuthorRelation(
                post = post,
                author = users.find { it.id == post.authorId }
            )
        }
    }

    override fun getPostsFlow(): Flow<List<PostWithAuthorRelation>> =
        combine(postsTable, usersTable) { posts, users ->
            mapListToRelation(
                posts = posts.sortedByDescending { it.createdAt },
                users = users
            )
        }

    override fun getFollowingPostsFlow(): Flow<List<PostWithAuthorRelation>> =
        combine(postsTable, usersTable) { posts, users ->
            val followedUsersIds = users.filter { it.isFollowedByCurrentUser }.map { it.id }
            val followedUsersPosts = posts.filter { followedUsersIds.contains(it.authorId) }
            mapListToRelation(
                posts = followedUsersPosts.sortedByDescending { it.createdAt },
                users = users
            )
        }

    override fun getSavedPostsFlow(): Flow<List<PostWithAuthorRelation>> =
        combine(postsTable, usersTable) { posts, users ->
            val savedPosts = posts.filter { it.isSavedByUser }
            mapListToRelation(
                posts = savedPosts.sortedByDescending { it.createdAt },
                users = users
            )
        }

    override suspend fun getPostById(postId: String): PostWithAuthorRelation? {
        val post = postsTable.value.find { it.id == postId } ?: return null
        val user = usersTable.value.find { it.id == post.authorId } ?: return null
        return PostWithAuthorRelation(post, user)
    }

    override fun getPostsByAuthorFlow(userId: String): Flow<List<PostWithAuthorRelation>> =
        combine(postsTable, usersTable) { posts, users ->
            val posts = posts.filter { it.authorId == userId }
            mapListToRelation(
                posts = posts.sortedByDescending { it.createdAt },
                users = users.filter { it.id == userId }
            )
        }

    override fun getPostsWithAuthorByIds(postIds: List<String>): List<PostWithAuthorRelation> {
        val posts = postsTable.value.filter { postIds.contains(it.id) }
        return mapListToRelation(
            posts = posts.sortedByDescending { it.createdAt },
            users = usersTable.value
        )
    }

    override suspend fun upsertPosts(posts: List<PostEntity>) {
        val currentPosts = postsTable.value.toMutableList()

        posts.forEach { post ->
            currentPosts.removeAll { it.id == post.id }
            currentPosts.add(post)
        }

        postsTable.value = currentPosts
    }

    override suspend fun updateLocalVote(
        postId: String,
        voteValue: Int,
        newTotalAura: Int
    ) {
        postsTable.value = postsTable.value.map { post ->
            if (post.id == postId) post.copy(currentUserVote = voteValue, totalAura = newTotalAura)
            else post
        }
    }

    override suspend fun updateLocalSaveState(
        postId: String,
        isSaved: Boolean,
        savesCount: Int
    ) {
        postsTable.value = postsTable.value.map { post ->
            if (post.id == postId) post.copy(isSavedByUser = isSaved, savesCount = savesCount)
            else post
        }
    }

    override suspend fun updateLocalCommentsCount(postId: String, increment: Int) {
        postsTable.value = postsTable.value.map { post ->
            if (post.id == postId) post.copy(commentsCount = post.commentsCount + increment)
            else post
        }
    }

    override suspend fun deletePostById(postId: String) {
        postsTable.value = postsTable.value.filter { it.id != postId }
    }

    override suspend fun deleteAllPosts() {
        postsTable.value = emptyList()
    }
}