package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: String): Post? = repository.getPostById(postId = postId)
}