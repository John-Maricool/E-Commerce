package com.maricoolsapps.e_commerce.utils

import android.app.Activity
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.content.Context
import android.net.Uri
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
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


fun Activity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.showAlertDialog(title: String, content: String): AlertDialog.Builder? {
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(content)
}