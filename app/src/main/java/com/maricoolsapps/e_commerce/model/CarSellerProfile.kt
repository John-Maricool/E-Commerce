package com.maricoolsapps.e_commerce.model

data class CarSellerProfile(
    var seller: CarBuyerOrSeller?,
    val sellerProducts: List<Product>?,
    val followers: Int?,
    val following: Int?) {
}