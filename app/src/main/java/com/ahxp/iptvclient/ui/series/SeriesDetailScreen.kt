package com.ahxp.iptvclient.ui.series

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ahxp.iptvclient.data.model.Episode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailScreen(
    seriesId: Int,
    viewModel: SeriesDetailViewModel = viewModel(),
    onEpisodeClick: (String, String) -> Unit, // streamId, extension
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(seriesId) {
        viewModel.fetchSeriesInfo(seriesId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.seriesInfo?.info?.name ?: "Series Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val info = uiState.seriesInfo
                if (info != null) {
                    // Robustly determine season numbers from both seasons metadata and episodes map
                    val seasonNumbers = remember(info) {
                        val metaSeasons = info.seasons.map { it.seasonNumber }
                        val episodeSeasons = info.episodes.keys.mapNotNull { it.toIntOrNull() }
                        (metaSeasons + episodeSeasons).distinct().sorted()
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Series Info Header
                        item {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = info.info.cover,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(120.dp)
                                        .aspectRatio(2f / 3f),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = info.info.name,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = info.info.plot ?: "No description available.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 5,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Seasons List (Merged source of truth)
                        items(seasonNumbers) { seasonNum ->
                            val seasonMeta = info.seasons.find { it.seasonNumber == seasonNum }
                            val episodes = info.episodes[seasonNum.toString()] ?: emptyList()

                            ExpandableSeasonItem(
                                seasonNumber = seasonNum,
                                seasonName = seasonMeta?.name,
                                episodes = episodes,
                                onEpisodeClick = onEpisodeClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableSeasonItem(
    seasonNumber: Int,
    seasonName: String?,
    episodes: List<Episode>,
    onEpisodeClick: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val title = if (!seasonName.isNullOrBlank()) seasonName else "Season $seasonNumber"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row (Clickable to expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            // Episodes List
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    if (episodes.isEmpty()) {
                        Text(
                            text = "No episodes found.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        episodes.forEach { episode ->
                            EpisodeItem(episode = episode, onClick = {
                                onEpisodeClick(episode.id, episode.containerExtension)
                            })
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EpisodeItem(episode: Episode, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = episode.title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
