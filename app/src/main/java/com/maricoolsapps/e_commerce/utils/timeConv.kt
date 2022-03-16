package com.maricoolsapps.e_commerce.utils

import java.text.SimpleDateFormat


val  SECOND_MILLIS = 1000
    val MINUTE_MILLIS = 60 * SECOND_MILLIS
   val  HOUR_MILLIS = 60 * MINUTE_MILLIS
    val DAY_MILLIS = 24 * HOUR_MILLIS

   fun Long.toTimeAgo(): String? {
       var time = this
        if (this < 1000000000000L) {
            time  =  this * 1000;
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }


        val diff = now - time
       return when {
           diff < MINUTE_MILLIS -> {
               "just now"
           }
           diff < 2 * MINUTE_MILLIS -> {
               "a minute ago"
           }
           diff < 50 * MINUTE_MILLIS -> {
               "${diff / MINUTE_MILLIS} minutes ago"
           }
           diff < 90 * MINUTE_MILLIS -> {
               "an hour ago"
           }
           diff < 24 * HOUR_MILLIS -> {
               "${diff / HOUR_MILLIS} hours ago"
           }
           diff < 48 * HOUR_MILLIS -> {
               "yesterday"
           }
           else -> {
               val dateFormat =
                   SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
               dateFormat.format(time)
               //"${diff / DAY_MILLIS} days ago"
           }
       }
    }