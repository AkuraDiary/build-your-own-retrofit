package com.asthiseta.diyretrofit.networking

import com.asthiseta.diyretrofit.networking.client.Client
import com.asthiseta.diyretrofit.networking.client.Client.Builder
import com.asthiseta.diyretrofit.networking.parser.JsonParser

object Config {

    const val BASE_URL = "http://10.132.76.120/BromoAirline/api/"

    val client :Client = Builder()
        .setUrl(BASE_URL)
        .setParser(JsonParser())
        .build()


}