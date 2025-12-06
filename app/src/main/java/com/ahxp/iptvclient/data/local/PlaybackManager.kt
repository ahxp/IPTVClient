package com.ahxp.iptvclient.data.local

import android.content.Context
import android.content.SharedPreferences

class PlaybackManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("playback_prefs", Context.MODE_PRIVATE)

    fun savePosition(streamId: String, position: Long) {
        // Only save if position is greater than 10 seconds (10000ms) to avoid accidental resumes from start
        if (position > 10_000) {
            prefs.edit().putLong(streamId, position).apply()
        }
    }

    fun getPosition(streamId: String): Long {
        return prefs.getLong(streamId, 0L)
    }

    fun clearPosition(streamId: String) {
        prefs.edit().remove(streamId).apply()
    }
}
