package com.maricoolsapps.e_commerce.di

import android.content.Context
import androidx.room.CoroutinesRoom
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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

/*
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
*/

    @Provides
    @Singleton
    fun provideFirebaseStorage():FirebaseStorage{
        return FirebaseStorage.getInstance()
    }
}