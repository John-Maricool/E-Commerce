package com.maricoolsapps.e_commerce.utils

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.interfaces.OnViewSelectListener

fun View.toggleVisibility(value: Boolean) {
    if (value) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
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

fun Activity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.showAlertDialog(title: String, content: String): AlertDialog.Builder? {
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(content)
}

fun TabLayout.onSelectListener(listener: OnViewSelectListener){
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
        override fun onTabSelected(tab: TabLayout.Tab?) {
            listener.onSelect(tab?.text.toString())
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            listener.onNoSelect()
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            listener.onSelect(tab?.text.toString())
        }

    })
}