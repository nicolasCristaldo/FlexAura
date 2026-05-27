package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.repository.UserRepository
import javax.inject.Inject


class SyncUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> =
        repository.syncUserProfile(userId = userId)
}