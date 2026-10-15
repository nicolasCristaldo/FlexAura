package com.flexaura.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.FlexAuraTheme

@Composable
fun FlexAuraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    trailingIconDescription: String,
    modifier: Modifier = Modifier,
    trailingIconRes: Int = R.drawable.ic_clear,
    onTrailingIconClick: () -> Unit = { onValueChange("") },
    isError: Boolean = false,
    errorMessage: String? = null,
    forceErrorVisibility: Boolean = false,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var isDirty by rememberSaveable { mutableStateOf(false) }
    val showError = isError && (isDirty || forceErrorVisibility)

    Column(
        verticalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingSmall),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )

        TextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                if (!isDirty) isDirty = true
            },
            isError = showError,
            singleLine = singleLine,
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onTrailingIconClick() },
                        modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeMedium)
                    ) {
                        Icon(
                            painter = painterResource(id = trailingIconRes),
                            contentDescription = trailingIconDescription,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            supportingText = {
                if (showError) {
                    Text(
                        text = errorMessage ?: stringResource(R.string.error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .2f),
                errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = .5f),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = Color.Transparent,
            ),
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth()
        )
    }
}