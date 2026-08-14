package com.flexaura.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flexaura.data.domain.usecase.post.SearchPostsUseCase
import com.flexaura.data.domain.usecase.post.ToggleSavePostUseCase
import com.flexaura.data.domain.usecase.post.VotePostUseCase
import com.flexaura.data.domain.usecase.user.SearchUsersUseCase
import com.flexaura.search.SearchUiEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchPostsUseCase: SearchPostsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val votePostUseCase: VotePostUseCase,
    private val toggleSavePostUseCase: ToggleSavePostUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SearchUiEffect>()
    val uiEffect: SharedFlow<SearchUiEffect> get() = _uiEffect.asSharedFlow()

    private var searchJob: Job? = null

    fun onIntent(intent: SearchUiIntent) = when(intent) {
        is SearchUiIntent.OnSearchClicked -> {
            searchJob?.cancel()
            search(intent.searchText)
        }
        is SearchUiIntent.OnTabChanged -> _uiState.update { it.copy(selectedTab = intent.tab) }
        is SearchUiIntent.OnTextChange -> {
            _uiState.update { it.copy(query = intent.newText) }
            dispatchDebounceSearch(intent.newText)
        }
        is SearchUiIntent.Refresh -> search(uiState.value.query)
        is SearchUiIntent.OnPostClicked -> emitEffect(NavigateToPostDetail(intent.postId))
        is SearchUiIntent.OnAuthorClicked -> emitEffect(NavigateToAuthorProfile(intent.authorId))
        is SearchUiIntent.OnSavedClicked -> toggleSavePost(intent.postId)
        is SearchUiIntent.OnVoteClicked -> votePost(
            postId = intent.postId,
            vote = intent.vote,
            currentVote = intent.currentVote
        )
    }

    private fun dispatchDebounceSearch(query: String) {
        searchJob?.cancel()

        if(query.isBlank()) {
            _uiState.update {
                it.copy(usersResults = emptyList(), postsResults = emptyList(), isSearching = false)
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            search(query)
        }
    }

    private fun search(query: String) = viewModelScope.launch {
        if(query.isBlank()) return@launch
        _uiState.update { it.copy(isSearching = true) }

        try {
            val usersDeferred = async { searchUsersUseCase(query, 1, 10) }
            val postsDeferred = async { searchPostsUseCase(query, 1, 10) }

            val usersResult = usersDeferred.await()
            val postsResult = postsDeferred.await()

            _uiState.update {
                it.copy(
                    postsResults = postsResult.getOrDefault(emptyList()),
                    usersResults = usersResult.getOrDefault(emptyList()),
                    isSearching = false,
                    errorMessage = null
                )
            }

            if(usersResult.isFailure || postsResult.isFailure) {
                emitEffect(ShowSnackBar("Something went wrong"))
            }
        }
        catch (e: Exception) {
            _uiState.update { it.copy(isSearching = false, errorMessage = e.localizedMessage) }
            emitEffect(ShowSnackBar(e.localizedMessage ?: "Unknown error"))
        }
    }

    private fun toggleSavePost(postId: String) = viewModelScope.launch {
        toggleSavePostUseCase(postId = postId)
            .onFailure { error ->
                _uiEffect.emit(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun votePost(postId: String, vote: Int, currentVote: Int) = viewModelScope.launch {
        votePostUseCase(postId = postId, value = vote, currentVote = currentVote)
            .onFailure { error ->
                emitEffect(ShowSnackBar(error.localizedMessage ?: "Unknown error"))
            }
    }

    private fun emitEffect(effect: SearchUiEffect) = viewModelScope.launch {
        _uiEffect.emit(effect)
    }
}