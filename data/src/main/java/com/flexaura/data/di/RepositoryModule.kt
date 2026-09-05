package com.flexaura.data.di

import com.flexaura.data.domain.repository.AuthRepository
import com.flexaura.data.domain.repository.CommentRepository
import com.flexaura.data.domain.repository.MediaRepository
import com.flexaura.data.repository.CommentRepositoryImpl
import com.flexaura.data.domain.repository.PostRepository
import com.flexaura.data.repository.PostRepositoryImpl
import com.flexaura.data.domain.repository.UserRepository
import com.flexaura.data.repository.AuthRepositoryImpl
import com.flexaura.data.repository.MediaRepositoryImpl
import com.flexaura.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        mediaRepository: MediaRepositoryImpl
    ): MediaRepository
}