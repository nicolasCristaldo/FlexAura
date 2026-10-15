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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flexaura.components.general.FlexAuraTextField
import com.flexaura.core.designsystem.theme.FlexAuraTheme
import com.flexaura.core.designsystem.R as DesignR
import com.flexaura.features.auth.R as AuthR
import com.flexaura.core.designsystem.theme.appCircularGradientBackground
import com.flexaura.data.domain.util.isValidDisplayName
import com.flexaura.data.domain.util.isValidEmail
import com.flexaura.data.domain.util.isValidPassword
import com.flexaura.data.domain.util.isValidUsername
import com.flexaura.features.auth.components.AuthButton
import com.flexaura.features.auth.components.AuthenticationFooter

@Composable
internal fun RegisterRoute(
    onNavigateToLogin: (String?) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is RegisterUiEffect.NavigateToLogin -> onNavigateToLogin(effect.email)
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
            .padding(FlexAuraTheme.dimens.paddingLarge)
    ) {
        Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingLarge))

        Text(
            text = stringResource(AuthR.string.register_screen_title),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(bottom = FlexAuraTheme.dimens.paddingSmall)
        )

        Text(
            text = stringResource(AuthR.string.register_screen_subtitle),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingExtraLarge))

        FlexAuraTextField(
            value = state.email,
            onValueChange = { onIntent(RegisterUiIntent.OnEmailChanged(it)) },
            label = stringResource(AuthR.string.email_label),
            isError = !state.email.isValidEmail(),
            errorMessage = stringResource(AuthR.string.invalid_email),
            forceErrorVisibility = state.showErrors,
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth(.9f)
        )

        FlexAuraTextField(
            value = state.displayName,
            onValueChange = { onIntent(RegisterUiIntent.OnDisplayNameChanged(it)) },
            label = stringResource(DesignR.string.display_name_label),
            isError = !state.displayName.isValidDisplayName(),
            errorMessage = stringResource(DesignR.string.invalid_display_name),
            forceErrorVisibility = state.showErrors,
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth(.9f)
        )

        FlexAuraTextField(
            value = state.username,
            onValueChange = { onIntent(RegisterUiIntent.OnUsernameChanged(it)) },
            label = stringResource(DesignR.string.username_label),
            isError = !state.username.isValidUsername(),
            errorMessage = stringResource(DesignR.string.invalid_username),
            forceErrorVisibility = state.showErrors,
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth(.9f)
        )

        FlexAuraTextField(
            value = state.password,
            onValueChange = { onIntent(RegisterUiIntent.OnPasswordChanged(it)) },
            label = stringResource(AuthR.string.password_label),
            isError = !state.password.isValidPassword(),
            errorMessage = stringResource(AuthR.string.invalid_password),
            forceErrorVisibility = state.showErrors,
            visualTransformation = PasswordVisualTransformation(),
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth(.9f)
        )

        FlexAuraTextField(
            value = state.confirmPassword,
            onValueChange = { onIntent(RegisterUiIntent.OnConfirmPasswordChanged(it)) },
            label = stringResource(AuthR.string.confirm_password_label),
            isError = state.showPasswordsMismatch,
            errorMessage = stringResource(AuthR.string.passwords_mismatch),
            visualTransformation = PasswordVisualTransformation(),
            trailingIconDescription = stringResource(DesignR.string.clear_content_desc),
            modifier = Modifier.fillMaxWidth(.9f)
        )

        Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingLarge))

        if (state.isLoading) CircularProgressIndicator()
        else AuthButton(
            title = stringResource(AuthR.string.register_button),
            onClick = { onIntent(RegisterUiIntent.OnRegisterClicked) },
            modifier = Modifier.fillMaxWidth(.6f)
        )

        Spacer(modifier = Modifier.height(FlexAuraTheme.dimens.paddingExtraLarge))

        AuthenticationFooter(
            text = stringResource(AuthR.string.register_footer_text),
            actionText = stringResource(AuthR.string.register_footer_action),
            onActionClick = { onIntent(RegisterUiIntent.OnLoginNavigated) }
        )
    }
}