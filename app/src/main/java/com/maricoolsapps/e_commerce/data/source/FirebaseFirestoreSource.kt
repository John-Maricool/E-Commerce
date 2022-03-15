package com.maricoolsapps.e_commerce.data.source

import android.database.CharArrayBuffer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.maricoolsapps.e_commerce.data.model.*
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Resource
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirebaseFirestoreSource
@Inject constructor(var cloud: FirebaseFirestore) {

    private val sellerCollection = cloud.collection(Constants.sellerorbuyer)
    private val carCollection = cloud.collection(Constants.car)
    private val chatChannel = cloud.collection(Constants.channels)

    suspend fun getCarsFromBrand(brand: String): QuerySnapshot? {
        return carCollection.document(brand)
            .collection(Constants.models).orderBy("time")
            .get(Source.SERVER).await()
    }

    suspend fun getCarsIdFromSeller(seller: String, brand: String): QuerySnapshot? {
        return sellerCollection.document(seller)
            .collection(brand).get(Source.DEFAULT).await()
    }

    suspend fun getUser(name: String): DocumentSnapshot? {
        return sellerCollection.document(name)
            .get(Source.SERVER).await()
    }

    fun getFollowersIdSnap(name: String): CollectionReference {
        return sellerCollection.document(name)
            .collection(Constants.followers)
    }

    fun getFollowingIdSnap(name: String): CollectionReference {
        return sellerCollection.document(name)
            .collection(Constants.following)
    }


    suspend fun getFollowersId(name: String): QuerySnapshot? {
        return sellerCollection.document(name)
            .collection(Constants.followers).get().await()
    }

    suspend fun getFollowingId(name: String): QuerySnapshot? {
        return sellerCollection.document(name)
            .collection(Constants.following).get().await()
    }

    suspend fun changeProfile(id: String, person: CarBuyerOrSeller): Void? {
        return sellerCollection
            .document(id).update(
                "name", person.name,
                "email", person.email,
                "phoneNumber", person.phoneNumber,
                "state", person.state,
                "businessLocation", person.businessLocation,
                "image", person.image
            ).await()
    }

    suspend fun changeProfileNew(id: String, person: CarBuyerOrSeller): Void? {
        return sellerCollection
            .document(id).set(person).await()
    }

    suspend fun removeUserProducts(name: String, brand: String, id: String): Void? {
        return sellerCollection.document(name)
            .collection(brand).document(id).delete().await()
    }

    suspend fun removeProducts(brand: String, id: String): Void? {
        return carCollection.document(brand)
            .collection(Constants.models).document(id).delete().await()
    }

    suspend fun addUserToFollowers(userId: Follow, userToFollow: Follow): Void? {
        return sellerCollection.document(userToFollow.name)
            .collection(Constants.followers).document(userId.name).set(userId).await()
    }

    suspend fun addUserToFollowing(userId: Follow, userToFollow: Follow): Void? {
        return sellerCollection.document(userId.name)
            .collection(Constants.following).document(userToFollow.name).set(userToFollow).await()
    }

    suspend fun removeFromFollowers(userIdOrName: String, userToUnfollow: String): Void? {
        return sellerCollection.document(userToUnfollow)
            .collection(Constants.followers).document(userIdOrName).delete().await()
    }

    suspend fun removeFromFollowing(userIdOrName: String, userToUnfollow: String): Void? {
        return sellerCollection.document(userIdOrName)
            .collection(Constants.following).document(userToUnfollow).delete().await()
    }

    suspend fun getFollowedUserId(userId: String, userToFollow: String): DocumentSnapshot? {
        return sellerCollection.document(userId)
            .collection(Constants.following).document(userToFollow).get().await()
    }

    suspend fun getCar(brand: String, id: String): DocumentSnapshot? {
        return carCollection.document(brand)
            .collection(Constants.models).document(id).get(Source.SERVER).await()
    }

    suspend fun addCarToSellerList(product: Product, userId: String): Void? {
        val productId = ProductId(product.id)
        return sellerCollection.document(userId)
            .collection(product.brand).document(product.id).set(productId).await()
    }

    suspend fun addCarToDb(product: Product): Void? {
        return carCollection.document(product.brand)
            .collection(Constants.models).document(product.id).set(product).await()
    }

    suspend fun setBrandDetails(product: Product): Void? {
        val modelName = ProductId(product.brand)
        return carCollection.document(product.brand)
            .set(modelName).await()
    }

    suspend fun getFeedbacksForCar(brand: String, id: String): QuerySnapshot? {
        return carCollection.document(brand)
            .collection(Constants.models).document(id)
            .collection(Constants.feedback).get()
            .await()
    }

    suspend fun addFeedbackForCar(brand: String, feedback: Feedback): DocumentReference? {
        return carCollection.document(brand)
            .collection(Constants.models).document(feedback.productId)
            .collection(Constants.feedback).add(feedback).await()
    }

    suspend fun reportProduct(report: Report, product: Product): DocumentReference? {
        cloud.collection(Constants.report).document(report.id).set(report).await()
        return cloud.collection(Constants.report).document(report.id).collection(product.brand)
            .add(product).await()
    }

    suspend fun createOrGetChatChannel(userId: String, userToChat: String): ChatChannel? {
        val channel = sellerCollection.document(userId)
            .collection(Constants.chats).document(userToChat).get().await()

        return if (!channel.exists()) {
            //create new channel id
            val newDoc = cloud.collection(Constants.channels).document()
            //create new chat channel
            val chatChannelForUser = ChatChannel(newDoc.id, userToChat)
            //set chat channel in userId doc
            sellerCollection.document(userId)
                .collection(Constants.chats).document(userToChat).set(chatChannelForUser).await()
            //set chat channel in second userid doc
            val chatChannelForSecond = ChatChannel(newDoc.id, userId)
            sellerCollection.document(userToChat)
                .collection(Constants.chats).document(userId).set(chatChannelForSecond).await()
            //set chat channel in channels collection
            val chatChannel = ChatChannel(newDoc.id, "")
            cloud.collection(Constants.channels).document(newDoc.id).set(chatChannel).await()
            chatChannelForUser
        } else {
            channel.toObject(ChatChannel::class.java)
        }
    }

    suspend fun getAllChatsId(userId: String): QuerySnapshot? {
        return sellerCollection.document(userId)
            .collection(Constants.chats)
            .get().await()
    }

    fun getChatStatus(userId: String): DocumentReference {
        return sellerCollection.document(userId)
            .collection(Constants.status).document(Constants.status)
    }

    suspend fun changeUserStatus(userId: String, status: UserStatus): Void? {
        return sellerCollection.document(userId)
            .collection(Constants.status).document(Constants.status).set(status).await()
    }

    suspend fun getLastMessages(channelId: String): DocumentSnapshot? {
        return chatChannel
            .document(channelId)
            .collection(Constants.messages).document(Constants.lastMessage).get().await()
    }

    suspend fun filteredCar(
        brand: String,
        type: String,
        location: String,
        condition: String,
        lowestPrice: Long,
        highestPrice: Long
    ): QuerySnapshot? {

        return carCollection.document(brand).collection(Constants.models)
            .whereEqualTo("type", type).whereEqualTo("condition", condition)
            .whereEqualTo("location", location)
            //.whereGreaterThan("price", lowestPrice)
            .get().await()
    }

    fun getMessagesId(chatChannel: ChatChannel): Query {
        return this.chatChannel.document(chatChannel.channelId)
            .collection(Constants.messages).orderBy("time")
    }

    suspend fun sendMessage(chatChannel: ChatChannel, messages: Messages) {
        this.chatChannel.document(chatChannel.channelId)
            .collection(Constants.messages).add(messages).await()
        this.chatChannel.document(chatChannel.channelId)
            .collection(Constants.messages).document(Constants.lastMessage).set(messages).await()
    }

    fun tagAllMessagesSeen(chatChannel: ChatChannel): CollectionReference {
        return this.chatChannel.document(chatChannel.channelId)
            .collection(Constants.messages)
    }

    suspend fun updateCar(car: Product): Void? {
        return carCollection.document(car.brand)
            .collection(Constants.models).document(car.id).set(car).await()
    }

    fun addTokenToFirebase(userId: String, token: String) {
        sellerCollection.document(userId).update("registrationTokens", token)
    }

    fun checkIfUserHasNewMessages(userId: String): LiveData<Int> {
        val state = MutableLiveData<Int>()
        var count = 0
        sellerCollection.document(userId).collection(Constants.chats).get()
            .addOnSuccessListener { value ->
                val chats = value.toObjects(ChatChannel::class.java)
                run lit@{
                    chats.forEach {
                        chatChannel.document(it.channelId).collection(Constants.messages)
                            .document(Constants.lastMessage)
                            .addSnapshotListener { value, error ->
                                val message = value?.toObject(Messages::class.java)
                                if (!message?.seen!! && message.receiverId == userId) {
                                    count++
                                    return@addSnapshotListener
                                } else {
                                    count = 0
                                }
                            }

                    }
                }
                state.postValue(count)
            }
        return state
    }

}