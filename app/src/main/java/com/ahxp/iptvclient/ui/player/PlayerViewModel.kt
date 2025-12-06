package com.ahxp.iptvclient.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahxp.iptvclient.data.local.PlaybackManager
import com.ahxp.iptvclient.data.local.ProfileManager

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val profileManager = ProfileManager(application)
    private val playbackManager = PlaybackManager(application)

    fun getStreamUrl(streamId: String, type: String = "live", extension: String = ""): String {
        val activeProfile = profileManager.getActiveProfile() ?: return ""
        
        val serverUrl = activeProfile.serverUrl.trimEnd('/')
        val username = activeProfile.username
        val password = activeProfile.password
        
        return when (type) {
            "movie" -> {
                // http://domain:port/movie/username/password/stream_id.ext
                "$serverUrl/movie/$username/$password/$streamId.$extension"
            }
            "series" -> {
                // http://domain:port/series/username/password/stream_id.ext
                "$serverUrl/series/$username/$password/$streamId.$extension"
            }
            else -> {
                // Standard Xtream Codes Live Stream URL format:
                // http://domain:port/username/password/stream_id
                "$serverUrl/$username/$password/$streamId"
            }
        }
    }

    fun getSavedPosition(streamId: String): Long {
        return playbackManager.getPosition(streamId)
    }

    fun savePosition(streamId: String, position: Long) {
        playbackManager.savePosition(streamId, position)
    }
}
