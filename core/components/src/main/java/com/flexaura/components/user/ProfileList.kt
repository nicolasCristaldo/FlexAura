package com.flexaura.components.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flexaura.data.domain.model.User

@Composable
fun ProfileList(
    users: List<User>,
    onProfileClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(items = users, key = { it.id }) { user ->
            ProfileCard(
                user = user,
                onUserClicked = { userId ->
                    onProfileClicked(userId)
                }
            )
        }
    }
}