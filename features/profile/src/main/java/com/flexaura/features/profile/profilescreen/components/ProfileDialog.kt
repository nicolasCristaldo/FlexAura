package com.flexaura.features.profile.profilescreen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flexaura.components.general.ConfirmationDialog
import com.flexaura.features.profile.R

internal data class ProfileDialogConfig(
    val title: String,
    val text: String,
    val confirmText: String,
    val dismissText: String,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit
)

internal enum class ProfileDialogType {
    LOGOUT, DELETE_ACCOUNT
}

@Composable
internal fun ProfileDialog(
    dialogConfig: ProfileDialogConfig,
    modifier: Modifier = Modifier
) {
    ConfirmationDialog(
        title = dialogConfig.title,
        text = dialogConfig.text,
        onConfirm = dialogConfig.onConfirm,
        onDismiss = dialogConfig.onDismiss,
        confirmText = dialogConfig.confirmText,
        dismissText = dialogConfig.dismissText,
        modifier = modifier
    )
}

@Composable
internal fun getDialogConfig(
    dialogType: ProfileDialogType,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onDismiss: () -> Unit
): ProfileDialogConfig =
    when (dialogType) {
        ProfileDialogType.LOGOUT -> ProfileDialogConfig(
            title = stringResource(R.string.logout),
            text = stringResource(R.string.dialog_logout_text),
            confirmText = stringResource(R.string.logout),
            dismissText = stringResource(R.string.cancel),
            onConfirm = onLogout,
            onDismiss = onDismiss
        )
        ProfileDialogType.DELETE_ACCOUNT -> ProfileDialogConfig(
            title = stringResource(R.string.delete_account),
            text = stringResource(R.string.dialog_delete_account_text),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = onDeleteAccount,
            onDismiss = onDismiss
        )
    }