package com.flexaura.core.network.datasource.database.comment

import com.flexaura.core.network.datasource.auth.UserSession
import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.CommentWithAuthorDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [CommentRemoteDataSource] utilizing the Supabase client and user session for remote comment workflows.
 *
 * @property supabaseClient The Supabase client instance used for PostgREST database operations.
 * @property userSession Helper class to obtain the currently authenticated user's ID.
 */
@Singleton
class CommentRemoteDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val userSession: UserSession
): CommentRemoteDataSource {
    override suspend fun getComments(postId: String): List<CommentWithAuthorDto> =
        supabaseClient.postgrest["comments"]
            .select(
                columns = Columns.raw("""
                    *,
                    users(*)
                """.trimIndent())
            ) {
                filter { eq("post_id", postId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<CommentWithAuthorDto>()

    override suspend fun createComment(postId: String, content: String): CommentDto {
        val currentId = userSession.requireCurrentUserId()
        val comment = CommentDto(
            id = UUID.randomUUID().toString(),
            postId = postId,
            authorId = currentId,
            content = content
        )

        return supabaseClient.postgrest["comments"]
            .insert(comment) {
                select()
            }.decodeSingle<CommentDto>()
    }

    override suspend fun deleteComment(commentId: String) {
        supabaseClient.postgrest["comments"]
            .delete {
                filter { eq("id", commentId) }
            }
    }
}