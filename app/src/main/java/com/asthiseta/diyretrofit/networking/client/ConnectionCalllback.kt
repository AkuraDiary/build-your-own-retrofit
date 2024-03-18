package com.asthiseta.diyretrofit.networking.client

interface ConnectionCalllback<T> {
    fun onSuccess(response: String) : T
    fun onError(error: String)
}



