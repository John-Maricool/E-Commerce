package com.maricoolsapps.e_commerce.data.db

import com.maricoolsapps.e_commerce.data.source.FirebaseFirestoreSource
import com.maricoolsapps.e_commerce.data.model.*
import android.content.Context
import android.content.SharedPreferences
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.core.OnlineState
import com.maricoolsapps.e_commerce.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class CloudQueries
@Inject constructor(
    private val source: FirebaseFirestoreSource,
    @ApplicationContext val context: Context,
    private val prefs: SharedPreferences
) {

    suspend fun getCarsFromBrand(name: String, b: (Resource<List<ProductModel>>) -> Unit) {
        /*
        This function is responsible for getting all the
        cars from a brand in the database, It places tbe cars in the
        recycler view for selection
         */
        if (!context.checkForInternet()) {
            b.invoke(Resource.error(Constants.check_internet, null))
        } else {
            b.invoke(Resource.loading())
            try {
                val ans = source.getCarsFromBrand(name)?.toObjects(Product::class.java)
                if (ans != null) {
                    val cars = MapperImpl.mapAllToCache(ans)
                    if (cars.isNotEmpty()) {
                        b.invoke(Resource.success(cars))
                    } else {
                        b.invoke(Resource.error(Constants.no_data, null))
                    }
                } else {
                    b.invoke(Resource.error(Constants.no_data, null))
                }
            } catch (e: Exception) {
                b.invoke(Resource.error(Constants.check_internet, null))
            }
        }
    }

    suspend fun getCarsFromSeller(
        name: String, brand: String,
        b: (Resource<List<ProductModel>>) -> Unit
    ) {
        /*
        * This function is used to get all the cars from a particular seller
        * */
        val sellerCars = mutableListOf<ProductModel>()
        b.invoke(Resource.loading())
        try {
            source.getCarsIdFromSeller(name, brand)?.documents
                ?.forEach { snap ->
                    val ref = snap.reference.id
                    val ans = source.getCar(brand, ref)?.toObject(Product::class.java)
                    if (ans != null) {
                        val car: ProductModel = MapperImpl.mapToCache(ans)
                        sellerCars.add(car)
                    }
                }
            b.invoke(Resource.success(sellerCars.toList()))
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    suspend fun getSeller(name: String, b: (Resource<CarBuyerOrSeller>) -> Unit) {
        /*
        * This function makes it possible to get the seller of the product.
        * */
        if (!context.checkForInternet()) {
            b.invoke(Resource.error(Constants.check_internet, null))
        } else {
            b.invoke(Resource.loading())
            try {
                val result = source.getUser(name)?.toObject(CarBuyerOrSeller::class.java)
                b.invoke(Resource.success(result))
            } catch (e: Exception) {
                b.invoke(Resource.error(Constants.check_internet, null))
            }
        }
    }

    fun getNumberOfFollowers(userIdOrName: String, b: (Resource<Int>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.getFollowersIdSnap(userIdOrName).addSnapshotListener { value, error ->
                if (error != null) {
                    b.invoke(Resource.error(Constants.check_internet, null))
                } else {
                    val result = value?.documents?.size
                    b.invoke(Resource.success(result))
                }
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    fun getNumberOfFollowing(userIdOrName: String, b: (Resource<Int>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.getFollowingIdSnap(userIdOrName).addSnapshotListener { value, error ->
                if (error != null) {
                    b.invoke(Resource.error(Constants.check_internet, null))
                } else {
                    val result = value?.documents?.size
                    b.invoke(Resource.success(result))
                }
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    suspend fun changeProfile(id: String, person: CarBuyerOrSeller, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            val editor = prefs.edit()
            editor.putString(SharedPrefs.USER_NAME, person.name)
            editor.putString(SharedPrefs.USER_EMAIL, person.email)
            editor.putString(SharedPrefs.USER_PIC, person.image)
            editor.apply()
            source.changeProfile(id, person)
            val status = UserStatus(true, Date().time)
            source.changeUserStatus(id, status)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.message.toString(), null))
        }
    }

    suspend fun changeProfileNew(
        id: String,
        person: CarBuyerOrSeller,
        b: (Resource<String>) -> Unit
    ) {
        b.invoke(Resource.loading())
        try {
            val editor = prefs.edit()
            editor.putString(SharedPrefs.USER_NAME, person.name)
            editor.putString(SharedPrefs.USER_EMAIL, person.email)
            editor.putString(SharedPrefs.USER_PIC, person.image)
            editor.apply()
            source.changeProfileNew(id, person)
            val status = UserStatus(true, Date().time)
            source.changeUserStatus(id, status)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.message.toString(), null))
        }
    }

    suspend fun removeProduct(
        name: String,
        brand: String,
        id: String,
        b: (Resource<String>) -> Unit
    ) {
        if (!context.checkForInternet()) {
            b.invoke(Resource.error(Constants.check_internet, null))
        } else {
            b.invoke(Resource.loading())
            try {
                source.removeUserProducts(name, brand, id)
                source.removeProducts(brand, id)
                b.invoke(Resource.success(Constants.successful))
            } catch (e: Exception) {
                b.invoke(Resource.error(e.message.toString(), null))
            }
        }
    }

    suspend fun getFollowers(userIdOrName: String, b: (Resource<List<CarBuyerOrSeller>>) -> Unit) {
        val followers = mutableListOf<CarBuyerOrSeller>()
        b.invoke(Resource.loading())
        try {
            val docs = source.getFollowersId(userIdOrName)?.documents
            if (docs != null && docs.isNotEmpty()) {
                docs.forEach {
                    val person =
                        source.getUser(it.reference.id)?.toObject(CarBuyerOrSeller::class.java)
                    if (person != null) {
                        followers.add(person)
                    }
                }
                b.invoke(Resource.success(followers))
            } else {
                b.invoke(Resource.error(Constants.no_data, null))
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    suspend fun getFollowing(userIdOrName: String, b: (Resource<List<CarBuyerOrSeller>>) -> Unit) {
        val following = mutableListOf<CarBuyerOrSeller>()
        b.invoke(Resource.loading())
        try {
            val docs = source.getFollowingId(userIdOrName)?.documents
            if (docs != null && docs.isNotEmpty()) {
                docs.forEach {
                    val person =
                        source.getUser(it.reference.id)?.toObject(CarBuyerOrSeller::class.java)
                    if (person != null) {
                        following.add(person)
                    }
                }
                b.invoke(Resource.success(following))
            } else {
                b.invoke(Resource.error(Constants.no_data, null))
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    suspend fun followUser(
        userIdOrName: Follow,
        userToFollow: Follow,
        b: (Resource<String>) -> Unit
    ) {
        b.invoke(Resource.loading())
        try {
            source.addUserToFollowers(userIdOrName, userToFollow)
            source.addUserToFollowing(userIdOrName, userToFollow)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.error, null))
        }
    }

    suspend fun unfollowUser(
        userIdOrName: String,
        userToUnfollow: String,
        b: (Resource<String>) -> Unit
    ) {
        b.invoke(Resource.loading())
        try {
            source.removeFromFollowers(userIdOrName, userToUnfollow)
            source.removeFromFollowing(userIdOrName, userToUnfollow)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun isUserFollowed(
        userId: String,
        userToFollow: String,
        b: (Resource<Boolean>) -> Unit
    ) {
        b.invoke(Resource.loading())
        try {
            val following = source.getFollowedUserId(userId, userToFollow)
            if (following != null) {
                if (following.exists()) {
                    b.invoke(Resource.success(true))
                } else {
                    b.invoke(Resource.error(Constants.error, false))
                }
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, false))
        }
    }

    suspend fun getCar(brand: String, id: String, b: (Resource<Product>) -> Unit) {
        if (!context.checkForInternet()) {
            b.invoke(Resource.error(Constants.check_internet, null))
        } else {
            b.invoke(Resource.loading())
            try {
                val result = source.getCar(brand, id)?.toObject(Product::class.java)
                b.invoke(Resource.success(result))
            } catch (e: Exception) {
                b.invoke(Resource.error(Constants.check_internet, null))
            }
        }
    }

    suspend fun addCarToDb(product: Product, userIdOrName: String, b: (Resource<String>) -> Unit) {
        if (!context.checkForInternet()) {
            b.invoke(Resource.error(Constants.check_internet, null))
        } else {
            b.invoke(Resource.loading())
            try {
                source.setBrandDetails(product)
                source.addCarToDb(product)
                source.addCarToSellerList(product, userIdOrName)
                b.invoke(Resource.success(Constants.successful))
            } catch (E: Exception) {
                b.invoke(Resource.error(E.toString(), null))
            }
        }
    }

    suspend fun getFeedbacksWithUserForCar(
        brand: String,
        id: String,
        b: (Resource<List<FeedbackWithUser>>) -> Unit
    ) {
        b.invoke(Resource.loading())
        val feedbackWithUser = mutableListOf<FeedbackWithUser>()
        try {
            val feedbacks = source.getFeedbacksForCar(brand, id)?.toObjects(Feedback::class.java)
            feedbacks?.forEach {
                val user = source.getUser(it.userId)?.toObject(CarBuyerOrSeller::class.java)
                val new = FeedbackWithUser(it, user!!)
                feedbackWithUser.add(new)
            }
            b.invoke(Resource.success(feedbackWithUser))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun addFeedback(brand: String, feedback: Feedback, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.addFeedbackForCar(brand, feedback)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun reportProduct(report: Report, product: Product, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.reportProduct(report, product)
            b.invoke(Resource.success("Your report has been submitted to Admin"))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun getFilteredCars(
        brand: String,
        type: String,
        location: String,
        condition: String,
        lowestPrice: Long,
        highestPrice: Long,
        b: (Resource<List<ProductModel>>) -> Unit
    ) {
        if (!context.checkForInternet()) {
            b.invoke(Resource.error(Constants.check_internet, null))
        } else {
            b.invoke(Resource.loading())
            try {
                val value =
                    source.filteredCar(brand, type, location, condition, lowestPrice, highestPrice)
                if (value != null && !value.isEmpty) {
                    val cars =
                        MapperImpl.mapAllToCache(value.toObjects(Product::class.java))
                    Log.d("people", cars.toString())
                    b.invoke(Resource.success(cars))
                } else {
                    b.invoke(Resource.error(Constants.no_data, null))
                }
            } catch (e: Exception) {
                Log.d("people", e.toString())
                b.invoke(Resource.error(e.toString(), null))
            }
        }
    }

    suspend fun createOrGetChatChannel(
        userId: String,
        userToChat: String,
        b: (Resource<ChatChannel>) -> Unit
    ) {
        b.invoke(Resource.loading())
        try {
            val result = source.createOrGetChatChannel(userId, userToChat)
            b.invoke(Resource.success(result))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun getAllChats(userId: String, b: (Resource<List<ChatList>>) -> Unit) {
        if (!context.checkForInternet()) {
            b.invoke(Resource.error(Constants.check_internet, null))
        } else {
            b.invoke(Resource.loading())
            val users = mutableListOf<ChatList>()
            try {
                val docs = source.getAllChatsId(userId)?.toObjects(ChatChannel::class.java)
                if (docs != null) {
                    docs.forEach { chatChannel ->
                        val user = source.getUser(chatChannel.personChatting)
                            ?.toObject(CarBuyerOrSeller::class.java)
                        val lastMessages = source.getLastMessages(chatChannel.channelId)
                            ?.toObject(Messages::class.java)
                        if (user != null && lastMessages != null){
                            val chatlist =
                                ChatList(user, lastMessages)
                            users.add(chatlist)
                        } else {
                            val chatList = ChatList(user!!, Messages())
                            users.add(chatList)
                        }
                        users.sortByDescending {
                            it.lastMessages.time
                        }
                    }
                    b.invoke(Resource.success(users))
                } else {
                    b.invoke(Resource.error("Empty Chat", null))
                }
            } catch (e: Exception) {
                b.invoke(Resource.error(b.toString(), null))
            }
        }
    }

    fun getUserOnlineStatus(userId: String, b: (Resource<UserStatus>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.getChatStatus(userId).addSnapshotListener { value, error ->
                if (value != null) {
                    if (value.exists()) {
                        b.invoke(Resource.success(value.toObject(UserStatus::class.java)))
                    }
                }
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    fun getAllMessages(chatChannel: ChatChannel, b: (Resource<List<Messages>>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.getMessagesId(chatChannel).addSnapshotListener { value, error ->
                if (value != null) {
                    val docs = value.toObjects(Messages::class.java).distinct().toList()
                    b.invoke(Resource.success(docs))
                } else {
                    b.invoke(Resource.error("No data", null))
                }
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

     fun tagAllMessagesSeen(
        userID: String,
        channel: ChatChannel,
        b: (Resource<String>) -> Unit
    ) {
        b.invoke(Resource.loading())
        try {
            source.tagAllMessagesSeen(channel).addSnapshotListener { value, _ ->
                value?.documents?.forEach {
                    if (!it["senderId"]?.equals(userID)!! && it["seen"]?.equals(false)!!)
                        it.reference.update("seen", true)
                }
                b.invoke(Resource.success(Constants.successful))
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun changeUserStatus(userId: String, status: UserStatus) {
        source.changeUserStatus(userId, status)
    }

    suspend fun updateCar(car: Product, b: (Resource<String>) -> Unit) {
        if (!context.checkForInternet()) {
            b.invoke(Resource.error(Constants.check_internet, null))
        } else {
            b.invoke(Resource.loading())
            try {
                source.updateCar(car)
                b.invoke(Resource.success(Constants.successful))
            } catch (e: Exception) {
                b.invoke(Resource.error(e.toString(), null))
            }
        }
    }

    fun addTokenToFirebase(userId: String, token: String, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.addTokenToFirebase(userId, token)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }
    fun checkIfUserHasNewMessages(userId: String): LiveData<Boolean>{
        return source.checkIfUserHasNewMessages(userId)
    }


}
