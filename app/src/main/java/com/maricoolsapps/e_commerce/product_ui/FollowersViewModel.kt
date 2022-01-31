package com.maricoolsapps.e_commerce.product_ui

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.ProductModel
import com.maricoolsapps.e_commerce.repos.FavRepository
import com.maricoolsapps.e_commerce.repos.ProductDetailRepo
import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import com.maricoolsapps.e_commerce.room_db.ProductDao
import com.maricoolsapps.e_commerce.utils.MapperImpl
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import javax.inject.Inject

@HiltViewModel
class FollowersViewModel
@Inject constructor(val cloudQueries: CloudQueries, val auth: FirebaseAuth) : ViewModel() {

    val user = auth.currentUser!!.uid

    fun getFollowers(id: String): LiveData<Resource<List<CarBuyerOrSeller>?>> {
        return cloudQueries.getFollowers(id)
    }

    fun getFollowing(id: String): LiveData<Resource<List<CarBuyerOrSeller>?>> {
        return cloudQueries.getFollowing(id)
    }
}