package com.flexaura.createpost.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

@Composable
internal fun PostContentInput(
    content: String,
    onContentChange: (String) -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (isError) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant
    val backgroundColor = if (isError) MaterialTheme.colorScheme.errorContainer
        else Color.Transparent

    Box(modifier = modifier) {
        if (content.isEmpty()) {
            Text(
                text = "What's on your mind?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f)
            )
        }

        BasicTextField(
            value = content,
            onValueChange = { onContentChange(it) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .background(backgroundColor)
        )
    }
}