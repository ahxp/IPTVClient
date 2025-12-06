package com.ahxp.iptvclient.ui.home

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

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val liveCategories: List<Category> = emptyList(),
    val vodCategories: List<Category> = emptyList(),
    val seriesCategories: List<Category> = emptyList()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val profileManager = ProfileManager(application)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        val activeProfile = profileManager.getActiveProfile()

        if (activeProfile == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "No active profile selected.")
            return
        }

        if (activeProfile.type == ProfileType.M3U) {
             // TODO: Implement M3U parsing logic
             _uiState.value = _uiState.value.copy(errorMessage = "M3U Support is coming soon!")
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
                
                // Fetch Live TV Categories
                val liveCats = api.getLiveCategories(username = username, password = password)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    liveCategories = liveCats
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load data: ${e.localizedMessage}"
                )
            }
        }
    }
}
