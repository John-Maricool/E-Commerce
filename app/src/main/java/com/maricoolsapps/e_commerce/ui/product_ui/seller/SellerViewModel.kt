package com.maricoolsapps.e_commerce.ui.product_ui.seller

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.CarSellerProfile
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.model.Follow
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import com.maricoolsapps.e_commerce.data.repositories.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel
@Inject constructor(
    val repo: SellerRepository,
    val cloud: CloudQueries,
    var auth: FirebaseAuth,
    var defaultRepo: DefaultRepository
) : ViewModel() {

    private val _result = MutableLiveData<CarSellerProfile?>()
    val result: LiveData<CarSellerProfile?> get() = _result

    private val _cars = MutableLiveData<List<ProductModel>?>()
    val cars: LiveData<List<ProductModel>?> get() = _cars

    private var _channelCreated = MutableLiveData<ChatChannel?>()
    val channelCreated: LiveData<ChatChannel?> get() = _channelCreated

    var userId = auth.currentUser?.uid

    fun sellerProfile(uid: String = userId.toString(), userToFollow: String) {
        viewModelScope.launch(IO) {
            repo.sellerProfile(uid, userToFollow) {
                defaultRepo.onResult(it)
                _result.postValue(it.data)
            }
        }
    }

    fun getCarsFromSeller(userId: String, brand: String) = viewModelScope.launch {
        cloud.getCarsFromSeller(userId, brand) {
            defaultRepo.onResult(it)
            _cars.postValue(it.data)
        }
    }

    fun followUser(userId: Follow, userToFollow: Follow) = viewModelScope.launch(Main) {
        cloud.followUser(userId, userToFollow) {
            defaultRepo.onResult(it)
        }
    }

    fun unfollowUser(userId: String? = this.userId, userToUnFollow: String) =
        viewModelScope.launch(Main) {
            cloud.unfollowUser(userId.toString(), userToUnFollow) {
                defaultRepo.onResult(it)
            }
        }

    fun callUser(phoneNo: String): Intent {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNo")
        return callIntent
    }

    fun createChatChannel(userId: String = this.userId.toString(), userToChat: String){
        if (userId == userToChat){
            return
        }
        viewModelScope.launch(IO){
            cloud.createOrGetChatChannel(userId, userToChat){
                defaultRepo.onResult(it)
                _channelCreated.postValue(it.data)
            }
        }
    }
}
