package com.flexaura.components.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.flexaura.core.designsystem.theme.FlexAuraTheme

data class StateInfo(
    val title: String,
    val text: String? = null,
    val actionText: String? = null,
    val onActionClick: (() -> Unit)? = null
)

@Composable
fun StateInfoScreen(
    stateInfo: StateInfo,
    modifier: Modifier = Modifier
) {
    StateInfoScreen(
        title = stateInfo.title,
        text = stateInfo.text,
        actionText = stateInfo.actionText,
        onActionClick = stateInfo.onActionClick,
        modifier = modifier
    )
}

@Composable
fun StateInfoScreen(
    title: String,
    modifier: Modifier = Modifier,
    text: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingSmall))

        text?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if(onActionClick != null && actionText != null) {
            Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingLarge))

            OutlinedButton(
                onClick = onActionClick,
                content = { Text(text = actionText) },
                shape = CircleShape
            )
        }
    }
}