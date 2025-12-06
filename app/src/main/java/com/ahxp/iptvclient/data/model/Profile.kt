package com.ahxp.iptvclient.data.model

import java.util.UUID

data class Profile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val serverUrl: String,
    val username: String? = null, // Null for M3U
    val password: String? = null, // Null for M3U
    val type: ProfileType
)

enum class ProfileType {
    XTREAM, M3U
}
