package com.maricoolsapps.e_commerce.ui.product_ui.followers

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowersViewModel
@Inject constructor(val cloudQueries: CloudQueries, val auth: FirebaseAuth) : ViewModel() {

    val user = auth.currentUser!!.uid

    /*fun getFollowers(id: String): LiveData<Resource<List<CarBuyerOrSeller>?>> {
      //  return cloudQueries.getFollowers(id)
    }

    fun getFollowing(id: String): LiveData<Resource<List<CarBuyerOrSeller>?>> {
        //return cloudQueries.getFollowing(id)
    }*/
}