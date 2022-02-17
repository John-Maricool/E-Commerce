package com.maricoolsapps.e_commerce.utils

import android.util.Patterns
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.toggleVisibility(value: Boolean){
    if (value){
        this.visibility = View.VISIBLE
    }else{
        this.visibility = View.GONE
    }
}

fun validateEmail(email: String): Boolean{
    return Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isNotEmpty()
}

fun validatePassword(text: String): Boolean{
    return (text.length > 6 || text.isNotEmpty())
}

fun View.displaySnack(text: String){
    Snackbar.make(this, text, Snackbar.LENGTH_SHORT).show()
}