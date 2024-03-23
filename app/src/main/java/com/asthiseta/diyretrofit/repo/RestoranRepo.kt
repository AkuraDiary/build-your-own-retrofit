package com.asthiseta.diyretrofit.repo

import com.asthiseta.diyretrofit.model.AkunModel
import com.asthiseta.diyretrofit.model.Ayam
import com.asthiseta.diyretrofit.model.CustomerReview
import com.asthiseta.diyretrofit.model.CustomerReviewRequest
import com.asthiseta.diyretrofit.model.RestaurantResponseModel
import com.asthiseta.diyretrofit.model.ReviewResponseModel
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
//             headers = mapOf("Authorization" to "Bearer ayamgoyeng"),
//             queryParams = mapOf("q" to "restaurant","username" to "admin", "password" to "admin" ), //{base url}/list?q=restaurant
//             requestBody = Client.buildRequestBody(
//                  Ayam("ayam", 10)
//             ),
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

    fun sendReview(
        review: CustomerReviewRequest,
        successCallback: (ReviewResponseModel?) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        Config.client.enqueue(
            endpoint = "review",
            method = Client.POST,
            requestBody = Client.buildRequestBody(review),
            callback = object : ConnectionCalllback<ReviewResponseModel?> {
                override fun onSuccess(response: ReviewResponseModel?) {
                    // update the UI here
                    successCallback(response)
                }

                override fun onError(error: String) {
                    // update the UI here or show toast
                    errorCallback(error)
                }
            }

        )
    }


    fun login(
        username: String,
        password: String,
        successCallback: (AkunModel?) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        Config.client.enqueue(
            endpoint = "login",
            method = Client.POST,
            queryParams = mapOf(
                "username" to username, "password" to password
            ),
            callback = object : ConnectionCalllback<AkunModel?> {
                override fun onSuccess(response: AkunModel?) {
                    successCallback(response)
                }

                override fun onError(error: String) {
                    errorCallback(error)

                }
            }
        )
    }
}