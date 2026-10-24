package com.flexaura.core.network.di

import com.flexaura.core.network.datasource.auth.AuthRemoteDataSource
import com.flexaura.core.network.datasource.auth.AuthRemoteDataSourceImpl
import com.flexaura.core.network.datasource.database.comment.CommentRemoteDataSource
import com.flexaura.core.network.datasource.database.comment.CommentRemoteDataSourceImpl
import com.flexaura.core.network.datasource.database.post.PostRemoteDataSource
import com.flexaura.core.network.datasource.database.post.PostRemoteDataSourceImpl
import com.flexaura.core.network.datasource.database.storage.StorageRemoteDataSource
import com.flexaura.core.network.datasource.database.storage.StorageRemoteDataSourceImpl
import com.flexaura.core.network.datasource.database.user.UserRemoteDataSource
import com.flexaura.core.network.datasource.database.user.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindCommentRemoteDataSource(
        commentRemoteDataSourceImpl: CommentRemoteDataSourceImpl
    ): CommentRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPostRemoteDataSource(
        postRemoteDataSourceImpl: PostRemoteDataSourceImpl
    ): PostRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindStorageRemoteDataSource(
        storageRemoteDataSourceImpl: StorageRemoteDataSourceImpl
    ): StorageRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        userRemoteDataSourceImpl: UserRemoteDataSourceImpl
    ): UserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource
}