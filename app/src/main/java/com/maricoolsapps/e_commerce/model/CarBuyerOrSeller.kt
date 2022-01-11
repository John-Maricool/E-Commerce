package com.maricoolsapps.e_commerce.model

import android.provider.ContactsContract

data class CarBuyerOrSeller
(
    val image: String?,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val state: String,
    val businessLocation: String?
){
    constructor(): this(null,"", "", "", "", "")
}