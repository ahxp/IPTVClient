package com.ahxp.iptvclient.ui.series

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahxp.iptvclient.data.local.ProfileManager
import com.ahxp.iptvclient.data.model.Series
import com.ahxp.iptvclient.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SeriesListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val series: List<Series> = emptyList()
)

class SeriesListViewModel(application: Application) : AndroidViewModel(application) {

    private val profileManager = ProfileManager(application)
    private val _uiState = MutableStateFlow(SeriesListUiState())
    val uiState: StateFlow<SeriesListUiState> = _uiState.asStateFlow()

    fun fetchSeries(categoryId: String) {
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
                val series = api.getSeries(
                    username = username,
                    password = password,
                    categoryId = categoryId
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    series = series
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load series: ${e.localizedMessage}"
                )
            }
        }
    }
}
