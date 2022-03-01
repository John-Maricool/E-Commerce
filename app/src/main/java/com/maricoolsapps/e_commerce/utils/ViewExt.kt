package com.maricoolsapps.e_commerce.utils

import android.app.Activity
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R

fun View.toggleVisibility(value: Boolean) {
    if (value) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun validateEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isNotEmpty()
}

fun validateTwoPasswords(password1: String, password2: String): Boolean {
    return validatePassword(password1) && validatePassword(password2) && password1 == password2
}

fun validatePassword(text: String): Boolean {
    return (text.length > 6 || text.isNotEmpty())
}

fun View.displaySnack(text: String) {
    Snackbar.make(this, text, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(this.resources.getColor(R.color.red, null))
        .show()
}

fun ImageView.setResourceCenterCrop(data: String) {
    Glide.with(this.context)
        .load(data)
        .circleCrop()
        .placeholder(R.drawable.ic_account_circle)
        .into(this)
}

fun ImageView.setResource(data: String) {
    Glide.with(this.context)
        .load(data)
        .centerCrop()
        .placeholder(R.drawable.car)
        .into(this)
}

fun Activity.showToast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}