package com.maricoolsapps.e_commerce.data.model

data class CarBuyerOrSeller
(
    var id: String,
    var image: String?,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val state: String,
    val businessLocation: String?,
    val registrationTokens: String
){
    constructor(): this("", null,"", "", "", "", "", "")
}