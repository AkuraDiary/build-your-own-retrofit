package com.asthiseta.diyretrofit.networking

import com.asthiseta.diyretrofit.networking.client.Client
import com.asthiseta.diyretrofit.networking.client.Client.Builder
import com.asthiseta.diyretrofit.networking.parser.JsonParser

object Config {

    const val BASE_URL = "https://restaurant-api.dicoding.dev/"

    val client :Client = Builder()
        .setUrl(BASE_URL)
        .setParser(JsonParser())
        .build()


}