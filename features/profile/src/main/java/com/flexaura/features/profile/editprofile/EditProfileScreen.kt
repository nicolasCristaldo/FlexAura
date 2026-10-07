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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraTextField
import com.flexaura.components.general.NavigateBackTopBar
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.core.designsystem.theme.appCircularGradientBackground
import com.flexaura.data.domain.util.isValidDisplayName
import com.flexaura.data.domain.util.isValidLength
import com.flexaura.data.domain.util.isValidUsername
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
                title = "Edit profile",
                modifier = Modifier.fillMaxHeight(.065f)
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.appCircularGradientBackground()
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(innerPadding)
            ) { CircularProgressIndicator() }

            state.error != null -> {
                StateInfoScreen(
                    title = "Could not load profile",
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .verticalScroll(scrollState)
            .imePadding()
            .padding(12.dp)
    ) {
        ProfileImagePicker(
            selectedImageUri = state.selectedImageUri ?: state.currentImageUrl,
            onImageSelected = { uri -> onIntent(EditProfileUiIntent.OnImageChanged(uri)) },
            modifier = Modifier.size(120.dp)
        )

        FlexAuraTextField(
            value = state.displayName,
            onValueChange = { onIntent(EditProfileUiIntent.OnDisplayNameChanged(it)) },
            label = "Display name",
            isError = !state.displayName.isValidDisplayName(),
            errorMessage = "Display name must be between 1 and 25 characters and cannot contain newlines",
            modifier = Modifier.fillMaxWidth()
        )

        FlexAuraTextField(
            value = state.username,
            onValueChange = { onIntent(EditProfileUiIntent.OnUsernameChanged(it)) },
            label = "Username",
            isError = !state.username.isValidUsername(),
            errorMessage = "Username must be between 3 and 15 characters and can only contain letters, numbers, and underscores",
            modifier = Modifier.fillMaxWidth()
        )

        FlexAuraTextField(
            value = state.bio,
            onValueChange = { onIntent(EditProfileUiIntent.OnBioChanged(it)) },
            label = "Bio",
            singleLine = false,
            isError = !state.bio.isValidLength(0, 200),
            errorMessage = "The biography cannot exceed 200 characters",
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onIntent(EditProfileUiIntent.OnSaveProfile) },
            enabled = state.saveButtonEnabled(),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(.6f)
        ) { Text(text = "Save") }
    }
}