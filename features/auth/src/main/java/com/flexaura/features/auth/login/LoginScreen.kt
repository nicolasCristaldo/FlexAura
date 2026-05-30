package com.flexaura.features.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.features.auth.components.AuthTextField
import com.flexaura.features.auth.components.LoginButton

@Composable
fun LoginRoute(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginUiEffect.NavigateToRegister -> onNavigateToRegister()
                is LoginUiEffect.NavigateToHome -> onNavigateToHome()
                is LoginUiEffect.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LoginScreen(
            state = state.value,
            onIntent = viewModel::handleIntent,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    onIntent: (LoginUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Log in")

        Spacer(modifier = Modifier.padding(16.dp))

        AuthTextField(
            value = state.email,
            onValueChange = { onIntent(LoginUiIntent.OnEmailChanged(it)) },
            label = "Email"
        )

        AuthTextField(
            value = state.password,
            onValueChange = { onIntent(LoginUiIntent.OnPasswordChanged(it)) },
            label = "Password",
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.padding(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            LoginButton(
                title = "Log in with Email",
                onClick = { onIntent(LoginUiIntent.OnLoginClicked) }
            )

            LoginButton(
                title = "Log in with Google",
                onClick = { onIntent(LoginUiIntent.OnGoogleLoginClicked) }
            )
        }

        Spacer(modifier = Modifier.padding(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?")

            Spacer(modifier = Modifier.padding(4.dp))

            TextButton(onClick = { onIntent(LoginUiIntent.OnRegisterNavigated) }) {
                Text("Register here")
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        state = LoginUiState(),
        onIntent = {}
    )
}