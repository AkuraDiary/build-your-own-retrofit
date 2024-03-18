package com.asthiseta.diyretrofit.model

data class RestaurantResponseModel(
    var count: Int?,
    var error: Boolean?,
    var message: String?,
    var restaurants: List<RestaurantModel>?
)