package com.maricoolsapps.e_commerce.data.model

import java.util.*

data class Feedback(
    val productId: String,
    val emoji: String,
    val review: String,
    val short_review: String,
    val userId: String,
    val time: Long
) {
    constructor(): this("", "", "", "", "", 0)
}