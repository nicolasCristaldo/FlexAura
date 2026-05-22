package com.flexaura.data.di

import com.flexaura.data.repository.comment.CommentRepository
import com.flexaura.data.repository.comment.CommentRepositoryImpl
import com.flexaura.data.repository.post.PostRepository
import com.flexaura.data.repository.post.PostRepositoryImpl
import com.flexaura.data.repository.user.UserRepository
import com.flexaura.data.repository.user.UserRepositoryImpl
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
}