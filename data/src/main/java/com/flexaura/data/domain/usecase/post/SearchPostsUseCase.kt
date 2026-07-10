package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class SearchPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(query: String, page: Int, pageSize: Int) =
        repository.searchPosts(query, page, pageSize)
}