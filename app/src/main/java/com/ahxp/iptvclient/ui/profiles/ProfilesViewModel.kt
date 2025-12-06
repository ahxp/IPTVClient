package com.ahxp.iptvclient.ui.profiles

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahxp.iptvclient.data.local.ProfileManager
import com.ahxp.iptvclient.data.model.Profile

class ProfilesViewModel(application: Application) : AndroidViewModel(application) {
    private val profileManager = ProfileManager(application)

    fun getProfiles(): List<Profile> {
        return profileManager.getAllProfiles()
    }

    fun setActiveProfile(profileId: String) {
        profileManager.setActiveProfile(profileId)
    }
}
