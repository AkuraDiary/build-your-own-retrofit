package com.asthiseta.diyretrofit.repo

import com.asthiseta.diyretrofit.model.RestaurantResponseModel
import com.asthiseta.diyretrofit.networking.Config
import com.asthiseta.diyretrofit.networking.client.Client
import com.asthiseta.diyretrofit.networking.client.ConnectionCalllback

object RestoranRepo {

    var restaurantResponseModel: RestaurantResponseModel? = null
    fun getRestaurants(
        successCallback: (RestaurantResponseModel?) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        Config.client.enqueue(
            endpoint = "list",
            method = Client.GET,
            // headers = mapOf("Authorization" to "Bearer ${Config.token here}")
            // queryParams = mapOf("q" to "restaurant"), //{base url}/list?q=restaurant
            callback = object : ConnectionCalllback<RestaurantResponseModel?> {
                override fun onSuccess(response: RestaurantResponseModel?) {
                    restaurantResponseModel = response
                    successCallback(response)
                }

                override fun onError(error: String) {
                    errorCallback(error)
                }
            }
        )
    }
}