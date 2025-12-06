package com.ahxp.iptvclient.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahxp.iptvclient.data.local.ProfileManager
import com.ahxp.iptvclient.data.model.Profile
import com.ahxp.iptvclient.data.model.ProfileType
import com.ahxp.iptvclient.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class LoginType { XTREAM, M3U }

data class LoginUiState(
    val profileName: String = "",
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val selectedTab: LoginType = LoginType.XTREAM
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val profileManager = ProfileManager(application)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onTabSelected(loginType: LoginType) {
        _uiState.value = LoginUiState(selectedTab = loginType) // Reset fields on tab change
    }

    fun onValueChanged(update: (LoginUiState) -> LoginUiState) {
        _uiState.value = update(_uiState.value).copy(errorMessage = null)
    }

    fun login() {
        when (_uiState.value.selectedTab) {
            LoginType.XTREAM -> loginXtream()
            LoginType.M3U -> loginM3u()
        }
    }

    private fun loginXtream() {
        val state = _uiState.value
        if (state.serverUrl.isBlank() || state.username.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please fill all fields")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val api = RetrofitClient.getClient(state.serverUrl)
                api.login(username = state.username, password = state.password)

                val profile = Profile(
                    name = state.profileName,
                    serverUrl = state.serverUrl,
                    username = state.username,
                    password = state.password,
                    type = ProfileType.XTREAM
                )
                profileManager.addProfile(profile)

                _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Login Failed: ${e.localizedMessage}")
            }
        }
    }

    private fun loginM3u() {
        val state = _uiState.value
        if (state.serverUrl.isBlank()) { // M3U only requires a URL
            _uiState.value = state.copy(errorMessage = "Please enter the M3U URL")
            return
        }
        
        val profile = Profile(
            name = state.profileName,
            serverUrl = state.serverUrl,
            type = ProfileType.M3U
        )
        profileManager.addProfile(profile)
        
        _uiState.value = _uiState.value.copy(loginSuccess = true)
    }
}
