package com.maricoolsapps.e_commerce.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    val type: String,
    val description: String,
    val state: String,
    val town: String,
    val id: String,
    val condition: String,
    val brand: String,
    val photo: String,
    val price: Long
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
        0)
}