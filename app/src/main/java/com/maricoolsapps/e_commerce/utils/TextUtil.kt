package com.maricoolsapps.e_commerce.utils

import android.util.Patterns


fun validateEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isNotEmpty()
}

fun validateTwoPasswords(password1: String, password2: String): Boolean {
    return validatePassword(password1) && validatePassword(password2) && password1 == password2
}

fun validatePassword(text: String): Boolean {
    return (text.length > 6 || text.isNotEmpty())
}