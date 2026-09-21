package com.flexaura.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flexaura.components.general.ConfirmationDialog

@Composable
fun PostOptionsMenu(
    onDeletePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Options",
            modifier = Modifier
                .size(18.dp)
                .clickable { showMenu = !showMenu }
        )
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    showDeleteDialog = true
                    showMenu = false
                }
            )
        }

        if (showDeleteDialog) {
            ConfirmationDialog(
                title = "Delete Post",
                text = "Are you sure you want to delete this post? This action cannot be undone.",
                onConfirm = {
                    showDeleteDialog = false
                    onDeletePost()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}