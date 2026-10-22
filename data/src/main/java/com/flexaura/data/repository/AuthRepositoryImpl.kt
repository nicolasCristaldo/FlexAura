package com.flexaura.data.repository

import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.database.dao.UserDao
import com.flexaura.core.network.datasource.auth.AuthRemoteDataSource
import com.flexaura.core.network.datasource.database.SupabaseRemoteDataSource
import com.flexaura.data.di.ApplicationScope
import com.flexaura.data.di.IoDispatcher
import com.flexaura.data.domain.repository.AuthRepository
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.mapper.toEntity
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource,
    private val userDao: UserDao,
    private val postDao: PostDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
): AuthRepository {
    override val authState: Flow<AuthState> =
        authRemoteDataSource.sessionStatus.map { status ->
            when(status) {
                is SessionStatus.Authenticated -> {
                    val userId = status.session.user?.id ?: ""
                    AuthState.Authenticated(userId)
                }
                is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
                else -> AuthState.Loading
            }
        }.flowOn(dispatcher)

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            authRemoteDataSource.signUpWithEmail(
                email = email,
                password =  password,
                username = username,
                displayName = displayName
            )
            Unit
        }
    }

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            authRemoteDataSource.signInWithEmail(email = email, password = password)
            val userId = supabaseRemoteDataSource.currentUserId ?: throw Exception("Login failed")
            val remoteUser = supabaseRemoteDataSource.getUser(userId = userId) ?: throw Exception("Login failed")

            userDao.upsertUser(user = remoteUser.toEntity())
        }
    }.await()

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> =
        scope.async(dispatcher) {
            runCatching {
                authRemoteDataSource.signInWithGoogle(idToken = idToken)

                val userId = supabaseRemoteDataSource.currentUserId
                    ?: throw Exception("Login failed")
                val remoteUser = supabaseRemoteDataSource.getUser(userId = userId)
                    ?: throw Exception("Login failed")

                userDao.upsertUser(user = remoteUser.toEntity())
            }
        }.await()

    override suspend fun logout(): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            authRemoteDataSource.signOut()

            userDao.deleteAllUsers()
            postDao.deleteAllPosts()
        }
    }.await()

    override suspend fun deleteAccount(): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            authRemoteDataSource.deleteAccount()

            userDao.deleteAllUsers()
            postDao.deleteAllPosts()
        }
    }.await()
}