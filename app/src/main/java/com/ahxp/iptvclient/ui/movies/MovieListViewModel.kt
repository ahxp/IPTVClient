package com.ahxp.iptvclient.ui.movies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahxp.iptvclient.data.local.ProfileManager
import com.ahxp.iptvclient.data.model.Movie
import com.ahxp.iptvclient.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MovieListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val movies: List<Movie> = emptyList()
)

class MovieListViewModel(application: Application) : AndroidViewModel(application) {

    private val profileManager = ProfileManager(application)
    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    fun fetchMovies(categoryId: String) {
        val activeProfile = profileManager.getActiveProfile()

        if (activeProfile == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "No active profile.")
            return
        }

        val url = activeProfile.serverUrl
        val username = activeProfile.username
        val password = activeProfile.password

        if (url.isBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Invalid profile data.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val api = RetrofitClient.getClient(url)
                val movies = api.getVodStreams(
                    username = username,
                    password = password,
                    categoryId = categoryId
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    movies = movies
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load movies: ${e.localizedMessage}"
                )
            }
        }
    }
}
