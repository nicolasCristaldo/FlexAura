package com.flexaura.features.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flexaura.core.designsystem.R

@Composable
internal fun LoginButtonsSection(
    isLoading: Boolean,
    onLoginWithEmailClicked: () -> Unit,
    onLoginWithGoogleClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            AuthButton(
                title = "Log in",
                onClick = onLoginWithEmailClicked,
                modifier = Modifier.fillMaxWidth(.3f)
            )

            Text(
                text = "Or",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(4.dp)
            )

            AuthButton(
                title = "Log in with Google",
                iconRes = R.drawable.ic_google,
                onClick = onLoginWithGoogleClicked,
                modifier = Modifier.fillMaxWidth(.9f)
            )
        }
    }
}

@Composable
internal fun AuthButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}