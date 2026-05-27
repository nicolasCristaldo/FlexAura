package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.model.User
import com.flexaura.data.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(userId: String): Flow<User?> =
        repository.getUserFlow(userId = userId)
}