package com.flexaura.core.network.di

import com.flexaura.core.network.datasource.auth.AuthRemoteDataSource
import com.flexaura.core.network.datasource.auth.AuthRemoteDataSourceImpl
import com.flexaura.core.network.datasource.database.SupabaseRemoteDataSource
import com.flexaura.core.network.datasource.database.SupabaseRemoteDataSourceImpl
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
    abstract fun bindSupabaseRemoteDataSource(
        supabaseRemoteDataSourceImpl: SupabaseRemoteDataSourceImpl
    ): SupabaseRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource
}