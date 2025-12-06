package com.ahxp.iptvclient.ui.series

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahxp.iptvclient.data.local.ProfileManager
import com.ahxp.iptvclient.data.model.SeriesInfo
import com.ahxp.iptvclient.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SeriesDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val seriesInfo: SeriesInfo? = null
)

class SeriesDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val profileManager = ProfileManager(application)
    private val _uiState = MutableStateFlow(SeriesDetailUiState())
    val uiState: StateFlow<SeriesDetailUiState> = _uiState.asStateFlow()

    fun fetchSeriesInfo(seriesId: Int) {
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
                val info = api.getSeriesInfo(
                    username = username,
                    password = password,
                    seriesId = seriesId
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    seriesInfo = info
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load details: ${e.localizedMessage}"
                )
            }
        }
    }
}
