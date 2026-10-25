package com.nicolascristaldo.flexaura.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.repository.AuthState
import com.flexaura.data.domain.usecase.auth.GetAuthUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel responsible for managing and providing the global authentication state of the application.
 *
 * @property authState A [StateFlow] representing the current [AuthState] used to control session flow and navigation.
 */
@HiltViewModel
internal class MainViewModel @Inject constructor(
    getAuthUserUseCase: GetAuthUserUseCase
) : ViewModel() {
    val authState: StateFlow<AuthState> = getAuthUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )
}