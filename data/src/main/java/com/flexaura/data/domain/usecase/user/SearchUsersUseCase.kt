package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.repository.UserRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(query: String, page: Int, pageSize: Int) =
        repository.searchUsers(query, page, pageSize)
}