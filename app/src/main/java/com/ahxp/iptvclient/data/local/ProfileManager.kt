package com.ahxp.iptvclient.data.local

import android.content.Context
import com.ahxp.iptvclient.data.model.Profile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileManager(context: Context) {
    private val prefs = context.getSharedPreferences("iptv_profiles", Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun getProfiles(): MutableList<Profile> {
        val json = prefs.getString("profiles_list", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Profile>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    private fun saveProfiles(profiles: List<Profile>) {
        val json = gson.toJson(profiles)
        prefs.edit().putString("profiles_list", json).apply()
    }

    fun addProfile(profile: Profile) {
        val profiles = getProfiles()
        profiles.add(profile)
        saveProfiles(profiles)
    }

    fun getAllProfiles(): List<Profile> {
        return getProfiles()
    }

    fun getProfile(profileId: String): Profile? {
        return getProfiles().find { it.id == profileId }
    }

    fun deleteProfile(profileId: String) {
        val profiles = getProfiles()
        profiles.removeAll { it.id == profileId }
        saveProfiles(profiles)
    }

    fun setActiveProfile(profileId: String) {
        prefs.edit().putString("active_profile_id", profileId).apply()
    }

    fun getActiveProfile(): Profile? {
        val activeId = prefs.getString("active_profile_id", null)
        return if (activeId != null) {
            getProfile(activeId)
        } else {
            null
        }
    }

    fun clearActiveProfile() {
        prefs.edit().remove("active_profile_id").apply()
    }
}
