package com.flexaura.features.profile.editprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraTextField
import com.flexaura.components.general.NavigateBackTopBar
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.core.designsystem.theme.appCircularGradientBackground
import com.flexaura.data.domain.util.isValidDisplayName
import com.flexaura.data.domain.util.isValidLength
import com.flexaura.data.domain.util.isValidUsername
import com.flexaura.features.profile.R as ProfileR
import com.flexaura.core.designsystem.R as DesignR
import com.flexaura.features.profile.editprofile.components.ProfileImagePicker

@Composable
internal fun EditProfileRoute(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is EditProfileUiEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }

                is EditProfileUiEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NavigateBackTopBar(
                onNavigateBack = onNavigateBack,
                title = stringResource(ProfileR.string.edit_profile_screen_title),
                modifier = Modifier.fillMaxHeight(.065f)
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.appCircularGradientBackground()
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) { CircularProgressIndicator() }

            state.error != null -> {
                StateInfoScreen(
                    title = stringResource(ProfileR.string.profile_error_load_title),
                    text = state.error,
                    modifier = Modifier.fillMaxSize()
                ) { viewModel.onIntent(EditProfileUiIntent.Init) }
            }

            else -> EditProfileScreen(
                state = state,
                onIntent = viewModel::onIntent,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun EditProfileScreen(
    state: EditProfileUiState,
    onIntent: (EditProfileUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FlexAuraTheme.dimens.paddingLarge),
        modifier = modifier
            .verticalScroll(scrollState)
            .imePadding()
            .padding(FlexAuraTheme.dimens.paddingMedium)
    ) {
        ProfileImagePicker(
            selectedImageUri = state.selectedImageUri ?: state.currentImageUrl,
            onImageSelected = { uri -> onIntent(EditProfileUiIntent.OnImageChanged(uri)) },
            modifier = Modifier.size(FlexAuraTheme.dimens.profileImageSizeLarge)
        )

        FlexAuraTextField(
            value = state.displayName,
            onValueChange = { onIntent(EditProfileUiIntent.OnDisplayNameChanged(it)) },
            label = stringResource(DesignR.string.display_name_label),
            isError = !state.displayName.isValidDisplayName(),
            errorMessage = stringResource(DesignR.string.invalid_display_name),
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth()
        )

        FlexAuraTextField(
            value = state.username,
            onValueChange = { onIntent(EditProfileUiIntent.OnUsernameChanged(it)) },
            label = stringResource(DesignR.string.username_label),
            isError = !state.username.isValidUsername(),
            errorMessage = stringResource(DesignR.string.invalid_username),
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth()
        )

        FlexAuraTextField(
            value = state.bio,
            onValueChange = { onIntent(EditProfileUiIntent.OnBioChanged(it)) },
            label = stringResource(ProfileR.string.bio_label),
            singleLine = false,
            isError = !state.bio.isValidLength(0, 200),
            errorMessage = stringResource(ProfileR.string.invalid_bio),
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onIntent(EditProfileUiIntent.OnSaveProfile) },
            enabled = state.saveButtonEnabled(),
            modifier = Modifier
                .padding(vertical = FlexAuraTheme.dimens.paddingLarge)
                .fillMaxWidth(.6f)
        ) { Text(text = stringResource(ProfileR.string.save_profile)) }
    }
}