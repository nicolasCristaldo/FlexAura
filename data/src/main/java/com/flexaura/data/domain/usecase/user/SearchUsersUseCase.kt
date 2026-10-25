package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.model.User
import com.flexaura.data.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for searching users.
 *
 * @property repository The repository for user-related operations.
 */
class SearchUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    /**
     * Searches for users with the specified query.
     *
     * @param query The search query.
     * @param page The page number for pagination.
     * @param pageSize The number of users per page.
     * @return A [Result] containing a list of [User]s or an exception if the search fails.
     */
    suspend operator fun invoke(query: String, page: Int, pageSize: Int): Result<List<User>> =
        repository.searchUsers(query, page, pageSize)
}