package com.ahxp.iptvclient.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("user_info")
    val userInfo: UserInfo,
    @SerializedName("server_info")
    val serverInfo: ServerInfo
)

data class UserInfo(
    @SerializedName("username")
    val username: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("exp_date")
    val expDate: String?,
    @SerializedName("is_trial")
    val isTrial: String?,
    @SerializedName("active_cons")
    val activeConnections: String?,
    @SerializedName("max_connections")
    val maxConnections: String?
)

data class ServerInfo(
    @SerializedName("url")
    val url: String?,
    @SerializedName("port")
    val port: String?,
    @SerializedName("https_port")
    val httpsPort: String?,
    @SerializedName("server_protocol")
    val serverProtocol: String?
)
