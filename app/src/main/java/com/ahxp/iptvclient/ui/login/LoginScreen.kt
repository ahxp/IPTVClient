package com.ahxp.iptvclient.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "IPTV Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        TabRow(selectedTabIndex = if (uiState.selectedTab == LoginType.XTREAM) 0 else 1) {
            Tab(
                selected = uiState.selectedTab == LoginType.XTREAM,
                onClick = { viewModel.onTabSelected(LoginType.XTREAM) },
                text = { Text("Xtream Codes") }
            )
            Tab(
                selected = uiState.selectedTab == LoginType.M3U,
                onClick = { viewModel.onTabSelected(LoginType.M3U) },
                text = { Text("M3U Playlist") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Common Field: Profile Name
        OutlinedTextField(
            value = uiState.profileName,
            onValueChange = { viewModel.onValueChanged { s -> s.copy(profileName = it) } },
            label = { Text("Profile Name (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.selectedTab == LoginType.XTREAM) {
            XtreamLoginFields(
                uiState = uiState,
                onUrlChange = { viewModel.onValueChanged { s -> s.copy(serverUrl = it) } },
                onUsernameChange = { viewModel.onValueChanged { s -> s.copy(username = it) } },
                onPasswordChange = { viewModel.onValueChanged { s -> s.copy(password = it) } }
            )
        } else {
            M3uLoginFields(
                uiState = uiState,
                onUrlChange = { viewModel.onValueChanged { s -> s.copy(serverUrl = it) } }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.selectedTab == LoginType.XTREAM) "Login" else "Save Playlist")
            }
        }
    }
}

@Composable
fun XtreamLoginFields(
    uiState: LoginUiState,
    onUrlChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = uiState.serverUrl,
            onValueChange = onUrlChange,
            label = { Text("Server URL (http://...)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun M3uLoginFields(
    uiState: LoginUiState,
    onUrlChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = uiState.serverUrl,
            onValueChange = onUrlChange,
            label = { Text("M3U URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
