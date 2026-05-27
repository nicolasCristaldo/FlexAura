package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.repository.UserRepository
import javax.inject.Inject

class ToggleFollowUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(targetUserId: String): Result<Unit> =
        repository.toggleFollowUser(targetUserId = targetUserId)
}