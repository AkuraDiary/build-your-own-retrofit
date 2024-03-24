package com.asthiseta.diyretrofit.model


data class RestaurantModel(
    var city: String? = null,
    var description: String? =  null,
    var id: String? =null,
    var name: String? = null,
    var pictureId: String? = null,
    var rating: Number? = null
)

data class RestaurantModelList(
    var data: List<RestaurantModel>? = null
)