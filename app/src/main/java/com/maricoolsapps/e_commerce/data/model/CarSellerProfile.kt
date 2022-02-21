package com.maricoolsapps.e_commerce.data.model

data class CarSellerProfile(
    var seller: CarBuyerOrSeller?,
    var followers: Int?,
    var following: Int?,
    var isFollowing: Boolean?
) {
}