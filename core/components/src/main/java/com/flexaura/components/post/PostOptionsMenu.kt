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
import androidx.compose.ui.res.stringResource
import com.flexaura.components.general.ConfirmationDialog
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.FlexAuraTheme

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
            contentDescription = stringResource(R.string.options_content_desc),
            modifier = Modifier
                .size(FlexAuraTheme.dimens.iconSizeSmall)
                .clickable { showMenu = !showMenu }
        )
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.delete),
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
                title = stringResource(R.string.dialog_delete_post_title),
                text = stringResource(R.string.dialog_delete_post_text),
                onConfirm = {
                    showDeleteDialog = false
                    onDeletePost()
                },
                onDismiss = { showDeleteDialog = false },
                confirmText = stringResource(R.string.delete),
                dismissText = stringResource(R.string.cancel)
            )
        }
    }
}