package com.flexaura.features.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraTextField
import com.flexaura.core.designsystem.theme.appCircularGradientBackground
import com.flexaura.data.domain.util.isValidDisplayName
import com.flexaura.data.domain.util.isValidEmail
import com.flexaura.data.domain.util.isValidPassword
import com.flexaura.data.domain.util.isValidUsername
import com.flexaura.features.auth.components.AuthButton
import com.flexaura.features.auth.components.AuthenticationFooter

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        modifier = Modifier.appCircularGradientBackground()
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
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create account",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Fill in your details to get started",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        FlexAuraTextField(
            value = state.email,
            onValueChange = { onIntent(RegisterUiIntent.OnEmailChanged(it)) },
            label = "Email",
            isError = !state.email.isValidEmail(),
            errorMessage = "Invalid email format.",
            forceErrorVisibility = state.showErrors,
            modifier = Modifier.fillMaxWidth(.9f)
        )

        FlexAuraTextField(
            value = state.displayName,
            onValueChange = { onIntent(RegisterUiIntent.OnDisplayNameChanged(it)) },
            label = "Display Name",
            isError = !state.displayName.isValidDisplayName(),
            errorMessage = "Display name must be between 1 and 25 characters and cannot contain newlines.",
            forceErrorVisibility = state.showErrors,
            modifier = Modifier.fillMaxWidth(.9f)
        )

        FlexAuraTextField(
            value = state.username,
            onValueChange = { onIntent(RegisterUiIntent.OnUsernameChanged(it)) },
            label = "Username",
            isError = !state.username.isValidUsername(),
            errorMessage = "Username must be between 3 and 15 characters and can only contain letters, numbers, and underscores.",
            forceErrorVisibility = state.showErrors,
            modifier = Modifier.fillMaxWidth(.9f)
        )

        FlexAuraTextField(
            value = state.password,
            onValueChange = { onIntent(RegisterUiIntent.OnPasswordChanged(it)) },
            label = "Password",
            isError = !state.password.isValidPassword(),
            errorMessage = "Password must be at least 6 characters.",
            forceErrorVisibility = state.showErrors,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(.9f)
        )

        FlexAuraTextField(
            value = state.confirmPassword,
            onValueChange = { onIntent(RegisterUiIntent.OnConfirmPasswordChanged(it)) },
            label = "Confirm Password",
            isError = state.showPasswordsMismatch,
            errorMessage = "Passwords do not match.",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(.9f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) CircularProgressIndicator()
        else AuthButton(
            title = "Register",
            onClick = { onIntent(RegisterUiIntent.OnRegisterClicked) },
            modifier = Modifier.fillMaxWidth(.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthenticationFooter(
            text = "Already have an account?",
            actionText = "Log in here",
            onActionClick = { onIntent(RegisterUiIntent.OnLoginNavigated) }
        )
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