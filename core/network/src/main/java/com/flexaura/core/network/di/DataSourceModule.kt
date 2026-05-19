package com.flexaura.core.network.di

import com.flexaura.core.network.datasource.SupabaseRemoteDataSource
import com.flexaura.core.network.datasource.SupabaseRemoteDataSourceImpl
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
}