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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.features.auth.R

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
                title = stringResource(R.string.login_button),
                onClick = onLoginWithEmailClicked,
                modifier = Modifier.fillMaxWidth(.6f)
            )

            Text(
                text = stringResource(R.string.login_divider_or),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(FlexAuraTheme.dimens.paddingExtraSmall)
            )

            AuthButton(
                title = stringResource(R.string.login_button_google),
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
                    modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeMedium)
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