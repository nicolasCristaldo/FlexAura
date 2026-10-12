package com.flexaura.features.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraTextField
import com.flexaura.core.designsystem.R
import com.flexaura.core.designsystem.theme.appCircularGradientBackground
import com.flexaura.data.domain.util.isValidEmail
import com.flexaura.data.domain.util.isValidPassword
import com.flexaura.features.auth.components.AuthenticationFooter
import com.flexaura.features.auth.components.LoginButtonsSection

@Composable
internal fun LoginRoute(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginUiEffect.NavigateToRegister -> onNavigateToRegister()
                is LoginUiEffect.NavigateToHome -> onNavigateToHome()
                is LoginUiEffect.LaunchGoogleSignIn -> {
                    launchGoogleSignIn(
                        context = context,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        snackBarHostState = snackBarHostState
                    )
                }
                is LoginUiEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        modifier = Modifier.appCircularGradientBackground()
    ) { paddingValues ->
        LoginScreen(
            state = state,
            onIntent = viewModel::handleIntent,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun LoginScreen(
    state: LoginUiState,
    onIntent: (LoginUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.showVerificationDialog) {
        AlertDialog(
            onDismissRequest = { onIntent(LoginUiIntent.OnDissmissVerificationDialog) },
            title = { Text(text = "Verify your email") },
            text = { Text(text = "We've sent a verification link to ${state.email}. Please check your inbox and follow the instructions to sign in.") },
            confirmButton = {
                TextButton(
                    onClick = { onIntent(LoginUiIntent.OnDissmissVerificationDialog) }
                ) { Text(text = "Got it") }
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(.5f))

        Text(
            text = "FlexAura",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Log in to your account",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.weight(.5f))

        FlexAuraTextField(
            value = state.email,
            onValueChange = { onIntent(LoginUiIntent.OnEmailChanged(it)) },
            label = "Email",
            isError = !state.email.isValidEmail(),
            errorMessage = "Invalid email.",
            forceErrorVisibility = state.showErrors,
            modifier = Modifier.fillMaxWidth(.9f)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        FlexAuraTextField(
            value = state.password,
            onValueChange = { onIntent(LoginUiIntent.OnPasswordChanged(it)) },
            label = "Password",
            isError = !state.password.isValidPassword(),
            trailingIconDescription = if (state.showPassword) "Hide password" else "Show password",
            trailingIconRes = if (state.showPassword) R.drawable.ic_visibility_off else R.drawable.ic_visibility,
            onTrailingIconClick = { onIntent(LoginUiIntent.OnShowPasswordChanged) },
            visualTransformation = if (state.showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            errorMessage = "Password must be at least 6 characters.",
            forceErrorVisibility = state.showErrors,
            modifier = Modifier.fillMaxWidth(.9f)
        )

        Spacer(modifier = Modifier.weight(.5f))

        LoginButtonsSection(
            isLoading = state.isLoading,
            onLoginWithEmailClicked = { onIntent(LoginUiIntent.OnLoginClicked) },
            onLoginWithGoogleClicked = { onIntent(LoginUiIntent.OnGoogleLoginClicked) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        AuthenticationFooter(
            text = "Don't have an account?",
            actionText = "Register here",
            onActionClick = { onIntent(LoginUiIntent.OnRegisterNavigated) }
        )
    }
}