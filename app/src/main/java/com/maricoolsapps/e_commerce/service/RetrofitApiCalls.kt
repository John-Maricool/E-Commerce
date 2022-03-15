package com.maricoolsapps.e_commerce.service

import androidx.lifecycle.LiveData
import com.maricoolsapps.e_commerce.data.model.SendNotification
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitApiCalls {

    @Headers(
        "Content_Type: application/json",
        "Authorization:key=AAAAZfrEm5g:APA91bHhvmAO332ujW7E8ZSPGj2iB4ru8pAnHs2FIwrXcxB1HrdWZOwfRTTqPsnhVbpq-D43SbTrulDHLysxWDPYkmTsTm8CpaTNX4Std_-o4qCB_HfkFH2fqUSHrstuWaBPizRYQPQe"
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification: SendNotification): Call<SendNotification>

}