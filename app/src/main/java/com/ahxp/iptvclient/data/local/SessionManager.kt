package com.ahxp.iptvclient.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("iptv_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROFILE_NAME = "profile_name"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_LOGIN_TYPE = "login_type" // "XTREAM" or "M3U"
    }

    fun saveXtreamSession(name: String, url: String, username: String, password: String) {
        prefs.edit().apply {
            putString(KEY_PROFILE_NAME, name)
            putString(KEY_SERVER_URL, url)
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putString(KEY_LOGIN_TYPE, "XTREAM")
            apply()
        }
    }

    fun saveM3uSession(name: String, url: String) {
        prefs.edit().apply {
            putString(KEY_PROFILE_NAME, name)
            putString(KEY_SERVER_URL, url)
            putString(KEY_LOGIN_TYPE, "M3U")
            // Clear Xtream specific fields
            remove(KEY_USERNAME)
            remove(KEY_PASSWORD)
            apply()
        }
    }

    fun getProfileName(): String? = prefs.getString(KEY_PROFILE_NAME, null)
    fun getServerUrl(): String? = prefs.getString(KEY_SERVER_URL, null)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getPassword(): String? = prefs.getString(KEY_PASSWORD, null)
    fun getLoginType(): String = prefs.getString(KEY_LOGIN_TYPE, "XTREAM") ?: "XTREAM"

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !getServerUrl().isNullOrBlank()
    }
}
