package com.maricoolsapps.e_commerce.utils

import android.app.Activity
import android.widget.ArrayAdapter
import com.maricoolsapps.e_commerce.R

fun Activity.getLexusAdapter(): ArrayAdapter<String> {
    val carModels = resources.getStringArray(R.array.Lexus)
    val spinnerAdapter = ArrayAdapter(
        this,
        R.layout.support_simple_spinner_dropdown_item,
        carModels
    )
    return spinnerAdapter
}

fun Activity.getAcuraAdapter(): ArrayAdapter<String> {
    val carModels = resources.getStringArray(R.array.Acura)
    val spinnerAdapter = ArrayAdapter(
        this,
        R.layout.support_simple_spinner_dropdown_item,
        carModels
    )
    return spinnerAdapter
}

fun Activity.getToyotaAdapter(): ArrayAdapter<String> {
    val carModels = resources.getStringArray(R.array.Toyota)
    val spinnerAdapter = ArrayAdapter(
        this,
        R.layout.support_simple_spinner_dropdown_item,
        carModels
    )
    return spinnerAdapter
}

fun Activity.getHondaAdapter(): ArrayAdapter<String> {
    val carModels = resources.getStringArray(R.array.Honda)
    val spinnerAdapter = ArrayAdapter(
        this,
        R.layout.support_simple_spinner_dropdown_item,
        carModels
    )
    return spinnerAdapter
}

fun Activity.getFordAdapter(): ArrayAdapter<String> {
    val carModels = resources.getStringArray(R.array.Ford)
    val spinnerAdapter = ArrayAdapter(
        this,
        R.layout.support_simple_spinner_dropdown_item,
        carModels
    )
    return spinnerAdapter
}

fun Activity.getBenzAdapter(): ArrayAdapter<String> {
    val carModels = resources.getStringArray(R.array.MercedesBenz)
    val spinnerAdapter = ArrayAdapter(
        this,
        R.layout.support_simple_spinner_dropdown_item,
        carModels
    )
    return spinnerAdapter
}

fun Activity.getBMWAdapter(): ArrayAdapter<String> {
    val carModels = resources.getStringArray(R.array.BMW)
    val spinnerAdapter = ArrayAdapter(
        this,
        R.layout.support_simple_spinner_dropdown_item,
        carModels
    )
    return spinnerAdapter
}