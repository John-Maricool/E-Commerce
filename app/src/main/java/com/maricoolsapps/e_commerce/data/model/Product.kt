package com.maricoolsapps.e_commerce.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val type: String,
    var description: String,
    var state: String,
    val ownerId: String,
    val location: String,
    val yearOfManufacturing: String?,
    val town: String,
    val id: String,
    var condition: String,
    val brand: String,
    val color: String,
    var price: Long,
    val rating: String,
    val photos: List<String>
) : Parcelable {
    constructor(): this(
        "",
        "",
        "",
       "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        "",
        listOf())
}