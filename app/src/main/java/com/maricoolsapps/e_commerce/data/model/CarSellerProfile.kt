package com.maricoolsapps.e_commerce.data.model

data class CarSellerProfile(
    var seller: CarBuyerOrSeller?,
    val followers: Int?,
    val following: Int?,
    val isFollowing: Boolean?
) {
}