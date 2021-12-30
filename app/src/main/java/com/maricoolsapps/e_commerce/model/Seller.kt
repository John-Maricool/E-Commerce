package com.maricoolsapps.e_commerce.model

data class Seller
(
    val businessName: String,
    val businessLocation: String
){
    constructor(): this("", "")
}