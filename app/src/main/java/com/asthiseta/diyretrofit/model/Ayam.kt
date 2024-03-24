package com.asthiseta.diyretrofit.model

data class Ayam (
    var ayam: String,
    var bebek : Int
)

data class RawListWrapper(
    var data : List<RestaurantModel>? = null
)