package com.ahxp.iptvclient.data.model

import com.google.gson.annotations.SerializedName

// --- Category --- //
data class Category(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int
)

// --- Live TV --- //
data class LiveChannel(
    @SerializedName("stream_id") val streamId: Int,
    val name: String,
    @SerializedName("stream_icon") val streamIcon: String,
    @SerializedName("epg_channel_id") val epgChannelId: String?,
    @SerializedName("category_id") val categoryId: String
)

// --- Movies (VOD) --- //
data class Movie(
    @SerializedName("stream_id") val streamId: Int,
    val name: String,
    @SerializedName("stream_icon") val streamIcon: String,
    val rating: String?,
    @SerializedName("category_id") val categoryId: String
)

// --- Series (for lists) --- //
data class Series(
    @SerializedName("series_id") val seriesId: Int,
    val name: String,
    val cover: String,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val genre: String?,
    val releaseDate: String?,
    @SerializedName("last_modified") val lastModified: String?,
    val rating: String?,
    @SerializedName("episode_run_time") val episodeRunTime: String?,
    @SerializedName("youtube_trailer") val youtubeTrailer: String?,
    @SerializedName("category_id") val categoryId: String
)

// --- Series (for detail view) --- //
data class SeriesInfo(
    val seasons: List<Season>,
    val info: Series,
    val episodes: Map<String, List<Episode>>
)

data class Season(
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("episode_count") val episodeCount: Int,
    val id: Int,
    val name: String,
    val overview: String?,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("cover_big") val coverBig: String?
)

data class Episode(
    val id: String,
    @SerializedName("season") val season: Int,
    val title: String,
    @SerializedName("container_extension") val containerExtension: String,
    val info: EpisodeInfo?
)

data class EpisodeInfo(
    val name: String?,
    @SerializedName("movie_image") val movieImage: String?
)
