package com.maricoolsapps.e_commerce.model

data class User(
     val email: String,
     val password: String) {
     constructor():this("", "")
}