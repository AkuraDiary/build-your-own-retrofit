package com.asthiseta.diyretrofit.model

data class RestaurantResponseModel(
    var count: Int? = null,
    var error: Boolean? = null,
    var message: String? = null,
    var restaurants: List<RestaurantModel>? = null
)