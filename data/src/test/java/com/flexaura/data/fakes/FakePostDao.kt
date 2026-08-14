package com.flexaura.data.fakes

import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.database.model.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakePostDao : PostDao {
    val postsTable = MutableStateFlow<List<PostEntity>>(emptyList())

    override fun getPostsFlow(): Flow<List<PostEntity>> =
        postsTable.map { list -> list.sortedByDescending { it.createdAt } }

    override suspend fun getPostById(postId: String): PostEntity? =
        postsTable.value.find { it.id == postId }

    override suspend fun upsertPosts(posts: List<PostEntity>) {
        val currentPosts = postsTable.value.toMutableList()

        posts.forEach { post ->
            currentPosts.removeAll { it.id == post.id }
            currentPosts.add(post)
        }

        postsTable.value = currentPosts
    }

    override suspend fun updateLocalVote(postId: String, voteValue: Int) {
        postsTable.value = postsTable.value.map { post ->
            if (post.id == postId) post.copy(currentUserVote = voteValue)
            else post
        }
    }

    override suspend fun updateLocalSaveState(postId: String, isSaved: Boolean) {
        postsTable.value = postsTable.value.map { post ->
            if (post.id == postId) post.copy(isSavedByUser = isSaved)
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