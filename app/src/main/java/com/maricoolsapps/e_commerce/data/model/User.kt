package com.maricoolsapps.e_commerce.data.model

data class User(
     val email: String,
     val password: String) {
     constructor():this("", "")
}