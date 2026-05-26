package com.flexaura.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flexaura.core.database.dao.CommentDao
import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.database.dao.UserDao
import com.flexaura.core.database.model.CommentEntity
import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.database.model.UserEntity

@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlexAuraDataBase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}