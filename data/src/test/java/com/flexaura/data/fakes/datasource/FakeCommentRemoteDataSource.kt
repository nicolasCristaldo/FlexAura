package com.flexaura.data.fakes.datasource

import com.flexaura.core.network.datasource.database.comment.CommentRemoteDataSource
import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.CommentWithAuthorDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.data.fakes.TestData
import com.flexaura.data.fakes.TestState
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

class FakeCommentRemoteDataSource(
    val state: TestState
) : CommentRemoteDataSource {
    private fun mapToCommentWithAuthor(comments: List<CommentDto>, users: List<UserDto>): List<CommentWithAuthorDto> =
        comments.map { comment ->
            CommentWithAuthorDto(
                id = comment.id,
                postId = comment.postId,
                authorId = comment.authorId,
                content = comment.content,
                createdAt = comment.createdAt,
                author = users.find { it.id == comment.authorId } ?: TestData.defaultUser
            )
        }

    override suspend fun getComments(postId: String): List<CommentWithAuthorDto> {
        val postComments = state.comments.value
            .filter { it.postId == postId }
            .sortedByDescending { it.createdAt }

        return mapToCommentWithAuthor(
            comments = postComments,
            users = state.users.value
        )
    }

    override suspend fun createComment(
        postId: String,
        content: String
    ): CommentDto {
        val newComment = TestData.createCommentDto(
            postId = postId,
            authorId = state.currentUserId,
            content = content,
            createdAt = Clock.System.now().toString()
        )

        state.comments.update { it + newComment }

        return newComment
    }

    override suspend fun deleteComment(commentId: String) {
        state.comments.update { comments -> comments.filter { it.id != commentId } }
    }
}