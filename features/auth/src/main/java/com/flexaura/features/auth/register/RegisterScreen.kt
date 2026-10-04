package com.flexaura.features.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.features.auth.components.AuthTextField

@Composable
internal fun RegisterRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is RegisterUiEffect.NavigateToLogin -> onNavigateToLogin()
                is RegisterUiEffect.NavigateToHome -> onNavigateToHome()
                is RegisterUiEffect.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        RegisterScreen(
            state = state.value,
            onIntent = viewModel::handleIntent,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun RegisterScreen(
    state: RegisterUiState,
    onIntent: (RegisterUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Register")

        Spacer(modifier = Modifier.padding(16.dp))

        AuthTextField(
            value = state.email,
            onValueChange = { onIntent(RegisterUiIntent.OnEmailChanged(it)) },
            label = "Email"
        )

        AuthTextField(
            value = state.password,
            onValueChange = { onIntent(RegisterUiIntent.OnPasswordChanged(it)) },
            label = "Password"
        )

        AuthTextField(
            value = state.username,
            onValueChange = { onIntent(RegisterUiIntent.OnUsernameChanged(it)) },
            label = "Username"
        )

        AuthTextField(
            value = state.displayName,
            onValueChange = { onIntent(RegisterUiIntent.OnDisplayNameChanged(it)) },
            label = "Display Name"
        )

        Spacer(modifier = Modifier.padding(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }
        else {
            Button(
                onClick = { onIntent(RegisterUiIntent.OnRegisterClicked) }
            ) {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account?")

            Spacer(modifier = Modifier.padding(4.dp))

            TextButton(
                onClick = { onIntent(RegisterUiIntent.OnLoginNavigated) }
            ) {
                Text("Log in here")
            }
        }
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    RegisterScreen(
        state = RegisterUiState(),
        onIntent = {}
    )
}