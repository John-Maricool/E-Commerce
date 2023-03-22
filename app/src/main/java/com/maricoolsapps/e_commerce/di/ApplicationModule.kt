package com.maricoolsapps.e_commerce.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.Preference
import androidx.room.CoroutinesRoom
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.maricoolsapps.e_commerce.room_db.ProductDao
import com.maricoolsapps.e_commerce.room_db.ProductDatabase
import com.maricoolsapps.e_commerce.service.RetrofitApiCalls
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope{
        return CoroutineScope(SupervisorJob())
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthentication(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences{
        return context.getSharedPreferences("Values", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideCarDb(@ApplicationContext context: Context): ProductDatabase =
       Room.databaseBuilder(
           context,
           ProductDatabase::class.java,
           "product_database"
       ).allowMainThreadQueries()
           .fallbackToDestructiveMigration()
           .build()

    @Provides
    @Singleton
    fun provideDao(productDatabase: ProductDatabase): ProductDao{
        return productDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage():FirebaseStorage{
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging{
        return FirebaseMessaging.getInstance()
    }

    companion object {
        private const val BASE_URL = "https://fcm.googleapis.com/"
    }
/*

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
*/

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): RetrofitApiCalls = retrofit.create(RetrofitApiCalls::class.java)

}