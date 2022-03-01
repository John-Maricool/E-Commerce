package com.maricoolsapps.e_commerce.utils

import android.net.ConnectivityManager
import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build

 fun Context.checkForInternet(): Boolean {

    // register activity with the connectivity manager service
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // if the android version is equal to M
    // or greater we need to use the
    // NetworkCapabilities to check what type of
    // network has the internet connection

     // Returns a Network object corresponding to
     // the currently active default data network.
     val network = connectivityManager.activeNetwork ?: return false

     // Representation of the capabilities of an active network.
     val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

     return when {
         // Indicates this network uses a Wi-Fi transport,
         // or WiFi has network connectivity
         activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

         // Indicates this network uses a Cellular transport. or
         // Cellular has network connectivity
         activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

         // else return false
         else -> false
     }
 }