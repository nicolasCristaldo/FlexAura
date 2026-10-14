package com.flexaura.createpost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraFab
import com.flexaura.components.general.NavigateBackTopBar
import com.flexaura.components.general.StateInfoScreen
import com.flexaura.components.general.rememberPhotoPickerLauncher
import com.flexaura.components.user.ProfileImage
import com.flexaura.components.user.UserIdentity
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.appCircularGradientBackground
import com.flexaura.createpost.CreatePostUiEffect.NavigateBack
import com.flexaura.createpost.CreatePostUiEffect.ShowSnackBar
import com.flexaura.createpost.CreatePostUiIntent.OnContentChange
import com.flexaura.createpost.CreatePostUiIntent.OnImageSelected
import com.flexaura.createpost.CreatePostUiIntent.OnRemoveImageClicked
import com.flexaura.createpost.CreatePostUiIntent.OnSubmitClicked
import com.flexaura.createpost.components.PostContentInput
import com.flexaura.createpost.components.SelectedImagePreview
import com.flexaura.data.domain.util.isValidLength

@Composable
internal fun CreatePostRoute(
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val launchPhotoPicker = rememberPhotoPickerLauncher(
        onImageSelected = { uri -> viewModel.onIntent(OnImageSelected(uri)) }
    )

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ShowSnackBar -> snackBarHostState.showSnackbar(effect.message)
                NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NavigateBackTopBar(
                onNavigateBack = onNavigateBack,
                title = "Create post",
                trailingContent = {
                    Button(
                        onClick = { viewModel.onIntent(OnSubmitClicked) },
                        enabled = state.isSubmitButtonEnabled
                    ) { Text("Create") }
                },
                modifier = Modifier.fillMaxHeight(.065f)
            )
        },
        floatingActionButton = {
            FlexAuraFab(
                onClick = { if(!state.isPostPublishing) launchPhotoPicker() },
                icon = R.drawable.ic_camera,
                contentDescription = "Pick image",
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.appCircularGradientBackground()
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) { CircularProgressIndicator() }
            state.errorMessage != null -> StateInfoScreen(
                title = "Something went wrong",
                text = state.errorMessage,
                onActionClick = { viewModel.onIntent(CreatePostUiIntent.Init) },
                modifier = Modifier.fillMaxWidth()
            )
            else -> CreatePostScreen(
                state = state,
                onIntent = viewModel::onIntent,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun CreatePostScreen(
    state: CreatePostUiState,
    onIntent: (CreatePostUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            ProfileImage(
                imageModel = state.currentUser?.imageUrl,
                size = 56
            )

            Spacer(modifier = Modifier.width(8.dp))

            UserIdentity(
                displayName = state.currentUser?.displayName ?: "",
                username = state.currentUser?.username ?: "",
                displayNameStyle = MaterialTheme.typography.titleLarge,
                usernameStyle = MaterialTheme.typography.bodyMedium
            )
        }

        PostContentInput(
            content = state.content,
            onContentChange = { onIntent(OnContentChange(it)) },
            focusRequester = focusRequester,
            isError = !state.content.isValidLength(0, 280),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (state.imageUri != null) {
            SelectedImagePreview(
                imageUri = state.imageUri,
                onRemoveImage = { onIntent(OnRemoveImageClicked) },
                modifier = Modifier.size(410.dp)
            )
        }
    }
}