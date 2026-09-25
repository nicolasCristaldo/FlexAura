package com.flexaura.features.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flexaura.components.user.ProfileImage
import com.flexaura.components.user.UserIdentity
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.negativeAuraColor
import com.flexaura.core.designsystem.theme.positiveAuraColor
import com.flexaura.core.designsystem.util.toCompactString
import com.flexaura.data.domain.model.User

@Composable
fun ProfileScreenHeader(
    user: User,
    isOwnProfile: Boolean,
    onToggleFollowClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage(
                imageUrl = user.imageUrl,
                size = 78,
                modifier = Modifier.padding(end = 12.dp)
            )

            UserIdentity(
                displayName = user.displayName,
                username = user.username,
                displayNameStyle = MaterialTheme.typography.headlineMedium,
                usernameStyle = MaterialTheme.typography.labelLarge
            )

            if (!isOwnProfile) {
                FollowButton(
                    onToggleFollow = onToggleFollowClicked,
                    iconRes = if (user.isFollowedByCurrentUser) R.drawable.ic_remove else R.drawable.ic_add,
                    contentDescription = if (user.isFollowedByCurrentUser) "Unfollow" else "Follow",
                    modifier = Modifier
                        .padding(start = 8.dp, top = 20.dp)
                        .align(Alignment.Top)
                )
            }

        }

        user.bio?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(.9f)
            )
        }

        ProfileStats(
            totalAura = user.totalAura,
            followersCount = user.followersCount,
            followingCount = user.followingCount,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProfileStats(
    totalAura: Int,
    followersCount: Int,
    followingCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        StatItem(
            value = totalAura,
            label = "Aura",
            icon = {
                val iconToShow = if (totalAura >= 0) R.drawable.ic_flame else R.drawable.ic_skull
                val tint = if (totalAura >= 0) positiveAuraColor else negativeAuraColor

                Icon(
                    painter = painterResource(id = iconToShow),
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(28.dp)
                )
            }
        )

        StatItem(
            value = followersCount,
            label = "Followers"
        )

        StatItem(
            value = followingCount,
            label = "Following"
        )
    }
}

@Composable
private fun StatItem(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value.toCompactString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = 4.dp)
            )

            icon()
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}