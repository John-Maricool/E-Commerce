package com.maricoolsapps.e_commerce.ui.product_ui.product_detail


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.model.FeedbackWithUser
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.repositories.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel
@Inject constructor(val auth: ProfileChanges, val cloud: CloudQueries, val defaultRepo: DefaultRepository) : ViewModel() {

    val userId = auth.getUserUid()
    private var _result = MutableLiveData<Product?>()
    val result: LiveData<Product?> get() = _result

    private var _done = MutableLiveData<List<FeedbackWithUser>?>()
    val done: LiveData<List<FeedbackWithUser>?> get() = _done

    private var _cars = MutableLiveData<List<ProductModel>?>()
    val cars: LiveData<List<ProductModel>?> get() = _cars

    private var _channelCreated = MutableLiveData<ChatChannel?>()
    val channelCreated: LiveData<ChatChannel?> get() = _channelCreated

    fun getCarAndFeedback(brand: String, id: String) {
        viewModelScope.launch(IO) {
            cloud.getCar(brand, id) {
                _result.postValue(it.data)
                defaultRepo.onResult(it)
            }
            cloud.getFeedbacksWithUserForCar(brand, id) {
                _done.postValue(it.data)
                defaultRepo.onResult(it)
            }
            cloud.getCarsFromBrand(brand){
                defaultRepo.onResult(it)
                _cars.postValue(it.data)
            }
        }
    }

    fun createChatChannel(userId: String = this.userId, userToChat: String){
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