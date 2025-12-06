package com.ahxp.iptvclient.ui.profiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ahxp.iptvclient.data.model.Profile

@Composable
fun ProfilesScreen(
    viewModel: ProfilesViewModel = viewModel(),
    onProfileSelected: () -> Unit,
    onAddProfileClick: () -> Unit
) {
    val profiles = remember { mutableStateOf(viewModel.getProfiles()) }

    LaunchedEffect(Unit) {
        // Refresh profiles when screen loads
        profiles.value = viewModel.getProfiles()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProfileClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Profile")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Who is watching?",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 32.dp)
            )

            if (profiles.value.isEmpty()) {
                Text("No profiles found. Please add one.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(profiles.value) { profile ->
                        ProfileItem(
                            profile = profile,
                            onClick = {
                                viewModel.setActiveProfile(profile.id)
                                onProfileSelected()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(profile: Profile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = profile.name.ifBlank { "Unnamed Profile" },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = profile.type.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
