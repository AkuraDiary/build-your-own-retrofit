package com.asthiseta.diyretrofit.networking.client

interface ConnectionCalllback<T> {
    fun onSuccess(response: T)
    fun onError(error: String)
}



