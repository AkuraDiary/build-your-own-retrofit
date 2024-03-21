package com.asthiseta.diyretrofit.model

data class ReviewResponseModel(
    var customerReviews: List<CustomerReview?>? = null,
    var error: Boolean? = null,
    var message: String? = null
)