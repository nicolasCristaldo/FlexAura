package com.flexaura.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PostRoute(
    postId: String,
    viewModel: PostScreenViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(postId) {
        viewModel.onIntent(PostDetailUiIntent.Init(postId))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is PostDetailUiEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
        )
    ) { innerPadding ->
        PostDetailScreen(
            state = state.value,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun PostDetailScreen(
    state: PostDetailUiState,
    onIntent: (PostDetailUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        else {
            state.post?.let { post ->
                Text(text = post.content ?: "")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = post.imageUrl ?: "")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Votes: ${post.totalAura}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Saves: ${post.savesCount}")
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    onClick = { onIntent(PostDetailUiIntent.OnSavedClicked(post.id)) }
                ) {
                    Icon(
                        imageVector = if (post.isSavedByUser) Icons.Default.Check
                        else Icons.Default.Close,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }
        }
    }
}