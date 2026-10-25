package com.flexaura.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flexaura.core.database.dao.CommentDao
import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.database.dao.UserDao
import com.flexaura.core.database.model.CommentEntity
import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.database.model.UserEntity

/**
 * Room database abstract class for the FlexAura application.
 *
 * Defines the core local database configuration, including entity declarations ([UserEntity], [PostEntity],
 * [CommentEntity]), database versioning, and abstract accessors for the application's Data Access Objects.
 */
@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlexAuraDataBase: RoomDatabase() {
    /**
     * Provides access to [UserDao] for managing local user profiles and follow states.
     */
    abstract fun userDao(): UserDao

    /**
     * Provides access to [PostDao] for managing local posts, feeds, and interactions.
     */
    abstract fun postDao(): PostDao

    /**
     * Provides access to [CommentDao] for managing post comments and local insertions/deletions.
     */
    abstract fun commentDao(): CommentDao
}