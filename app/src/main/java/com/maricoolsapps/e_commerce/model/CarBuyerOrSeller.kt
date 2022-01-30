package com.maricoolsapps.e_commerce.model

data class CarBuyerOrSeller
(
    var id: String,
    var image: String?,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val state: String,
    val businessLocation: String?
){
    constructor(): this("", null,"", "", "", "", "")
}