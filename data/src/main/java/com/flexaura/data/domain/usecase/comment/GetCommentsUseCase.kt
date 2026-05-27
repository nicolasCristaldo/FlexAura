package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.model.Comment
import com.flexaura.data.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    operator fun invoke(postId: String): Flow<List<Comment>> =
        repository.getCommentsFlow(postId = postId)
}