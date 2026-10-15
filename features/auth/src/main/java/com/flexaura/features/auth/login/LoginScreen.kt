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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraTextField
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.core.designsystem.R as DesignR
import com.flexaura.features.auth.R as AuthR
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
            title = { Text(text = stringResource(AuthR.string.dialog_verify_email_title)) },
            text = { Text(text = stringResource(AuthR.string.dialog_verify_email_desc, state.email)) },
            confirmButton = {
                TextButton(
                    onClick = { onIntent(LoginUiIntent.OnDissmissVerificationDialog) }
                ) { Text(text = stringResource(AuthR.string.dialog_verify_email_confirm)) }
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(FlexAuraTheme.dimens.paddingLarge)
    ) {
        Spacer(modifier = Modifier.weight(.5f))

        Text(
            text = stringResource(AuthR.string.login_screen_title),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = FlexAuraTheme.dimens.paddingSmall)
        )

        Text(
            text = stringResource(AuthR.string.login_screen_subtitle),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.weight(.5f))

        FlexAuraTextField(
            value = state.email,
            onValueChange = { onIntent(LoginUiIntent.OnEmailChanged(it)) },
            label = stringResource(AuthR.string.email_label),
            isError = !state.email.isValidEmail(),
            errorMessage = stringResource(AuthR.string.invalid_email),
            forceErrorVisibility = state.showErrors,
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth(.9f)
        )

        Spacer(modifier = Modifier.padding(FlexAuraTheme.dimens.paddingSmall))

        FlexAuraTextField(
            value = state.password,
            onValueChange = { onIntent(LoginUiIntent.OnPasswordChanged(it)) },
            label = stringResource(AuthR.string.password_label),
            isError = !state.password.isValidPassword(),
            trailingIconDescription = stringResource(
                if (state.showPassword) AuthR.string.hide_password_content_desc
                else AuthR.string.show_password_content_desc
            ),
            trailingIconRes = if (state.showPassword) AuthR.drawable.ic_visibility_off else AuthR.drawable.ic_visibility,
            onTrailingIconClick = { onIntent(LoginUiIntent.OnShowPasswordChanged) },
            visualTransformation = if (state.showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            errorMessage = stringResource(AuthR.string.invalid_password),
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
            text = stringResource(AuthR.string.login_footer_text),
            actionText = stringResource(AuthR.string.login_footer_action),
            onActionClick = { onIntent(LoginUiIntent.OnRegisterNavigated) }
        )
    }
}