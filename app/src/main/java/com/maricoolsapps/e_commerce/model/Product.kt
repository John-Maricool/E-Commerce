package com.maricoolsapps.e_commerce.model

import com.google.firebase.Timestamp

data class Product(
    val time: Timestamp?,
    val type: String,
    val model: String,
    val description: String,
    val price: String,
    val rating: Double,
    val photos: List<String>
) {
    constructor(): this(
        null,
        "",
        "",
        "",
        "",
        0.0,
        listOf())
}