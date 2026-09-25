package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(): Flow<List<Post>> = repository.getSavedPostsFlow()
}