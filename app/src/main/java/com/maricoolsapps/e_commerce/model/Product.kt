package com.maricoolsapps.e_commerce.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val type: String,
    val description: String,
    val state: String,
    val ownerId: String,
    val location: String,
    val yearOfManufacturing: String?,
    val town: String,
    val id: String,
    val condition: String,
    val brand: String,
    val color: String,
    val price: Long,
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