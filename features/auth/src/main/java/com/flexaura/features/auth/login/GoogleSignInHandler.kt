package com.flexaura.features.auth.login

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.flexaura.features.auth.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun launchGoogleSignIn(
    context: Context,
    viewModel: LoginViewModel,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState
) = coroutineScope.launch {
    try {
        val credentialManager = CredentialManager.create(context = context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        val result = credentialManager.getCredential(context = context, request = request)
        val credential = result.credential

        if(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            viewModel.onGoogleTokenReceived(googleIdTokenCredential.idToken)
        }
        else {
            viewModel.onGoogleSignInCancelled()
            snackBarHostState.showSnackbar("Google sign in failed")
        }
    }
    catch (e: GetCredentialCancellationException) {
        viewModel.onGoogleSignInCancelled()
    }
    catch (e: GetCredentialException) {
        viewModel.onGoogleSignInCancelled()
        snackBarHostState.showSnackbar(e.localizedMessage ?: "Google sign in failed")
    }
}