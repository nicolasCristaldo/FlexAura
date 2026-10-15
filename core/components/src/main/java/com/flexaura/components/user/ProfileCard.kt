package com.flexaura.components.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.data.domain.model.User

@Composable
fun ProfileCard(
    user: User,
    onUserClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onUserClicked(user.id) },
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .2f)
            ),
        shape = RoundedCornerShape(FlexAuraTheme.dimens.cornerRadiusLarge),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingExtraSmall),
            modifier = Modifier.padding(FlexAuraTheme.dimens.paddingSmall)
        ) {
            ProfileImage(
                imageModel = user.imageUrl,
                onClick = { onUserClicked(user.id) },
                modifier = Modifier.size(FlexAuraTheme.dimens.profileImageSizeSmall)
            )

            Column {
                UserIdentity(
                    displayName = user.displayName,
                    username = user.username,
                    onAuthorClicked = { onUserClicked(user.id) }
                )

                user.bio?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = FlexAuraTheme.dimens.paddingSmall)
                    )
                }
            }
        }
    }
}