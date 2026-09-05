package com.flexaura.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.flexaura.post.components.CommentsSection
import com.flexaura.post.PostDetailUiEffect.*
import com.flexaura.post.PostDetailUiIntent.*

@Composable
fun PostRoute(
    postId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAuthorProfile: (String) -> Unit,
    viewModel: PostScreenViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(postId) {
        viewModel.onIntent(Init(postId))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }
                is NavigateBack -> onNavigateBack()
                is NavigateToAuthorProfile -> onNavigateToAuthorProfile(effect.authorId)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal)
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
        } else {
            IconButton(
                onClick = { onIntent(OnNavigateBack) },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }

            state.post?.let { post ->
                if(post.author?.id == state.currentUserId) {
                    IconButton(
                        onClick = { onIntent(OnDeletePost(post.id)) }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
                Text(
                    text = post.author?.displayName ?: "Author not found",
                    modifier = if(post.author != null) Modifier.clickable {
                        onIntent(OnNavigateToAuthorProfile(post.author!!.id))
                    }else Modifier
                )
                Text(text = post.content ?: "")
                Spacer(modifier = Modifier.height(8.dp))
                if (post.imageUrl != null) {
                    AsyncImage(
                        model = post.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Votes: ${post.totalAura}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Saves: ${post.savesCount}")
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    onClick = { onIntent(OnSavedClicked(post.id)) }
                ) {
                    Icon(
                        imageVector = if (post.isSavedByUser) Icons.Default.Check
                        else Icons.Default.Close,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = state.commentContent,
                    onValueChange = { onIntent(OnCommentChanged(it)) },
                    placeholder = { Text(text = "Add a comment...") },
                    maxLines = 1,

                )
                Button(
                    onClick = { onIntent(OnSubmitComment(post.id, state.commentContent)) }
                ) {
                    Text(text = "Submit")
                }
                Spacer(modifier = Modifier.height(8.dp))
                CommentsSection(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
