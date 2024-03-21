package com.asthiseta.diyretrofit.model

data class CustomerReview(
    var date: String? = null,
    var name: String? = null,
    var review: String? = null
)

data class CustomerReviewRequest(
//{"id": string, "name": string, "review": string}
    var id: String? = null,
    var name: String? = null,
    var review: String? = null
)