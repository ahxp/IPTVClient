package com.ahxp.iptvclient.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun getClient(baseUrl: String): XtreamCodesApi {
        // Ensure the URL ends with a slash
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        return Retrofit.Builder()
            .baseUrl(formattedUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XtreamCodesApi::class.java)
    }
}
