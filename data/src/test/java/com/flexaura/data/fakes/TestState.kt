package com.flexaura.data.fakes

import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.SaveDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.VoteDto
import kotlinx.coroutines.flow.MutableStateFlow

class TestState {
    val users = MutableStateFlow<List<UserDto>>(emptyList())
    val posts = MutableStateFlow<List<PostDto>>(emptyList())
    val comments = MutableStateFlow<List<CommentDto>>(emptyList())
    val votes = MutableStateFlow<List<VoteDto>>(emptyList())
    val saves = MutableStateFlow<List<SaveDto>>(emptyList())

    val currentUserId = TestData.defaultUser.id

    fun clear() {
        users.value = emptyList()
        posts.value = emptyList()
        comments.value = emptyList()
    }
}