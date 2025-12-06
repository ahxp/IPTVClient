package com.ahxp.iptvclient.ui.movies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahxp.iptvclient.data.local.ProfileManager
import com.ahxp.iptvclient.data.model.Category
import com.ahxp.iptvclient.data.model.ProfileType
import com.ahxp.iptvclient.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MoviesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val categories: List<Category> = emptyList()
)

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val profileManager = ProfileManager(application)
    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        val activeProfile = profileManager.getActiveProfile()

        if (activeProfile == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "No active profile.")
            return
        }

        if (activeProfile.type == ProfileType.M3U) {
             _uiState.value = _uiState.value.copy(errorMessage = "M3U Support coming soon!")
             return
        }

        val url = activeProfile.serverUrl
        val username = activeProfile.username
        val password = activeProfile.password

        if (username.isNullOrBlank() || password.isNullOrBlank()) {
             _uiState.value = _uiState.value.copy(errorMessage = "Invalid credentials.")
             return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val api = RetrofitClient.getClient(url)
                val categories = api.getVodCategories(username = username, password = password)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    categories = categories
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load categories: ${e.localizedMessage}"
                )
            }
        }
    }
}
