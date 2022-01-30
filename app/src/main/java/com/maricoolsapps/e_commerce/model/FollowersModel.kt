package com.maricoolsapps.e_commerce.model

data class FollowersModel
(
    var image: String?,
    val name: String,
    val email: String
){
    constructor(): this(null,"", "")
}