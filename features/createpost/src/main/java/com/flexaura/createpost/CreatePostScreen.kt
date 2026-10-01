package com.flexaura.createpost

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.flexaura.components.general.FlexAuraFab
import com.flexaura.components.general.NavigateBackTopBar
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

@Composable
internal fun CreatePostRoute(
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onIntent(OnImageSelected(uri)) }
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
            val createPostButtonEnabled = state.content.isNotBlank() || state.imageUri != null

            NavigateBackTopBar(
                onNavigateBack = onNavigateBack,
                title = "Create post",
                trailingContent = {
                    Button(
                        onClick = { viewModel.onIntent(OnSubmitClicked) },
                        enabled = createPostButtonEnabled
                    ) { Text("Create") }
                },
                modifier = Modifier.fillMaxHeight(.065f)
            )
        },
        floatingActionButton = {
            FlexAuraFab(
                onClick = {
                    if(!state.isPostPublishing) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                },
                icon = R.drawable.ic_camera,
                contentDescription = "Pick image",
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.appCircularGradientBackground()
    ) { innerPadding ->
        CreatePostScreen(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(innerPadding)
        )
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
            if (state.isPostPublishing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

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

        Box(
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            if (state.content.isEmpty()) {
                Text(
                    text = "What's on your mind?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f)
                )
            }

            BasicTextField(
                value = state.content,
                onValueChange = { onIntent(OnContentChange(it)) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }

        if (state.imageUri != null) {
            Box(
                modifier = Modifier.size(410.dp)
            ) {
                AsyncImage(
                    model = state.imageUri,
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
                IconButton(
                    onClick = { onIntent(OnRemoveImageClicked) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Gray.copy(alpha = .4f)
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = Color.Black.copy(alpha = .8f),
                        contentDescription = "Remove image"
                    )
                }
            }
        }
    }
}