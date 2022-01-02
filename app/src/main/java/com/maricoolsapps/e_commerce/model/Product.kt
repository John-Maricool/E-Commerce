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
    val specifications: String,
    val price: String,
    val rating: String,
    val photos: List<String>?
): Parcelable {
    constructor(): this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        listOf())
}