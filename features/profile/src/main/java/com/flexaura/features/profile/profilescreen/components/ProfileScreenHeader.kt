package com.flexaura.features.profile.profilescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.flexaura.components.user.ProfileImage
import com.flexaura.components.user.UserIdentity
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.core.designsystem.R as DesignR
import com.flexaura.features.profile.R as ProfileR
import com.flexaura.core.designsystem.theme.negativeAuraColor
import com.flexaura.core.designsystem.theme.positiveAuraColor
import com.flexaura.core.designsystem.util.toCompactString
import com.flexaura.data.domain.model.User

@Composable
internal fun ProfileScreenHeader(
    user: User,
    isOwnProfile: Boolean,
    onToggleFollowClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingLarge),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileImage(
                imageModel = user.imageUrl,
                size = 78,
                modifier = Modifier.padding(end = FlexAuraTheme.dimens.paddingMedium)
            )

            UserIdentity(
                displayName = user.displayName,
                username = user.username,
                displayNameStyle = MaterialTheme.typography.headlineMedium,
                usernameStyle = MaterialTheme.typography.labelLarge,
                modifier = Modifier.widthIn(max = FlexAuraTheme.dimens.userIdentityMaxWidth)
            )

            if (!isOwnProfile) {
                FollowButton(
                    onToggleFollow = onToggleFollowClicked,
                    iconRes = if (user.isFollowedByCurrentUser) ProfileR.drawable.ic_remove else ProfileR.drawable.ic_add,
                    contentDescription = stringResource(
                        if (user.isFollowedByCurrentUser) ProfileR.string.unfollow
                        else ProfileR.string.follow
                    ),
                    modifier = Modifier
                        .padding(
                            start = FlexAuraTheme.dimens.paddingSmall,
                            top = FlexAuraTheme.dimens.paddingLarge
                        )
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
            label = stringResource(ProfileR.string.aura_label),
            icon = {
                val iconToShow = if (totalAura >= 0) DesignR.drawable.ic_flame else DesignR.drawable.ic_skull
                val tint = if (totalAura >= 0) positiveAuraColor else negativeAuraColor

                Icon(
                    painter = painterResource(id = iconToShow),
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(FlexAuraTheme.dimens.iconSizeLarge)
                )
            }
        )

        StatItem(
            value = followersCount,
            label = stringResource(ProfileR.string.followers_label)
        )

        StatItem(
            value = followingCount,
            label = stringResource(ProfileR.string.following_label)
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
                text = value.toCompactString(LocalContext.current),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = FlexAuraTheme.dimens.paddingExtraSmall)
            )

            icon()
        }

        Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingExtraSmall))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}