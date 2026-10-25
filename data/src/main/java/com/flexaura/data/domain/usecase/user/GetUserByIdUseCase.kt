package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.model.User
import com.flexaura.data.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a user by their ID.
 *
 * @property repository The repository for user-related operations.
 */
class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user.
     * @return A [Flow] emitting a [User] or null if not found.
     */
    operator fun invoke(userId: String): Flow<User?> =
        repository.getUserFlow(userId = userId)
}