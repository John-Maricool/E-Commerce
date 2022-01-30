package com.maricoolsapps.e_commerce.product_ui

import android.app.Activity
import android.view.View
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.model.CarSellerProfile
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.ProductModel
import com.maricoolsapps.e_commerce.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel
    @Inject constructor(val cloud: CloudQueries):ViewModel() {

    private val _result = MutableLiveData<CarSellerProfile>()
    val result get() = _result

    fun sellerProfile(userId: String, userToFollow: String) {
        viewModelScope.launch(Main) {
            val seller = viewModelScope.async(IO) {
                cloud.getSeller(userId)
            }

            val followers = viewModelScope.async(IO) {
                cloud.getNumberOfFollowers(userId)
            }

            val following = viewModelScope.async(IO) {
                cloud.getNumberOfFollowing(userId)
            }

            val isFollowed = viewModelScope.async(IO) {
                cloud.isUserFollowed(userId, userToFollow)
            }

            val sellerProfile = CarSellerProfile(seller.await().data,
                followers.await().data,
                following.await().data,
                isFollowed.await().data
            )
            _result.postValue(sellerProfile)
        }
    }

    fun getCarsFromSeller(userId: String, brand: String): LiveData<Resource<List<ProductModel>>> {
            return cloud.getCarsFromSeller(userId, brand)
    }

    fun showSnackBar(view: View, it: String, activity: Activity){
        Snackbar.make(view, it, Snackbar.LENGTH_LONG)
            .setBackgroundTint(activity.resources.getColor(R.color.pink2, null)).show()
    }

    fun followUser(userIdOrName: String, userToFollow: String) = viewModelScope.launch(Main) {
        cloud.followUser(userIdOrName, userToFollow)
    }

    fun unfollowUser(userIdOrName: String, userToUnFollow: String) = viewModelScope.launch(Main){
        cloud.unfollowUser(userIdOrName, userToUnFollow)
    }

}
