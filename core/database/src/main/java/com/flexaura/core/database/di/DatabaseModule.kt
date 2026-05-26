package com.flexaura.core.database.di

import android.content.Context
import androidx.room.Room
import com.flexaura.core.database.FlexAuraDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideFlexAuraDatabase(@ApplicationContext context: Context): FlexAuraDataBase =
        Room.databaseBuilder(
            context,
            FlexAuraDataBase::class.java,
            "flexaura-db"
        ).build()

    @Provides
    fun provideUserDao(flexAuraDataBase: FlexAuraDataBase) = flexAuraDataBase.userDao()

    @Provides
    fun providePostDao(flexAuraDataBase: FlexAuraDataBase) = flexAuraDataBase.postDao()

    @Provides
    fun provideCommentDao(flexAuraDataBase: FlexAuraDataBase) = flexAuraDataBase.commentDao()
}