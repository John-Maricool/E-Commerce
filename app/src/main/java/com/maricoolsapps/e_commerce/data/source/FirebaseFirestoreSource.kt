package com.maricoolsapps.e_commerce.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.maricoolsapps.e_commerce.data.model.*
import com.maricoolsapps.e_commerce.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFirestoreSource
@Inject constructor(var cloud: FirebaseFirestore) {

    suspend fun getCarsFromBrand(brand: String): QuerySnapshot? {
        return cloud.collection(Constants.car).document(brand)
            .collection(Constants.models).get().await()
    }

    suspend fun getCarsIdFromSeller(seller: String, brand: String): QuerySnapshot? {
        return cloud.collection(Constants.sellerorbuyer).document(seller)
            .collection(brand).get().await()
    }

    suspend fun getUser(name: String): DocumentSnapshot? {
        return cloud.collection(Constants.sellerorbuyer).document(name)
            .get().await()
    }

    suspend fun getFollowersId(name: String): QuerySnapshot? {
        return cloud.collection(Constants.sellerorbuyer).document(name)
            .collection(Constants.followers)
            .get().await()
    }

    suspend fun getFollowingId(name: String): QuerySnapshot? {
        return cloud.collection(Constants.sellerorbuyer).document(name)
            .collection(Constants.following)
            .get().await()
    }

    suspend fun changeProfile(id: String, person: CarBuyerOrSeller): Void? {
        return cloud.collection(Constants.sellerorbuyer)
            .document(id).set(person).await()
    }

    suspend fun removeUserProducts(name: String, brand: String, ids: List<String>){
        return ids.forEach {
            cloud.collection(Constants.sellerorbuyer).document(name)
                .collection(brand).document(it).delete().await()
        }
    }

    suspend fun removeProducts(brand: String, ids: List<String>){
        return ids.forEach{
            cloud.collection(Constants.car).document(brand)
                .collection(Constants.models).document(it).delete().await()
        }
    }

    suspend fun addUserToFollowers(userId: Follow, userToFollow: Follow): Void? {
        return cloud.collection(Constants.sellerorbuyer).document(userToFollow.name)
            .collection(Constants.followers).document(userId.name).set(userId).await()
    }

    suspend fun addUserToFollowing(userId: Follow, userToFollow: Follow): Void? {
        return cloud.collection(Constants.sellerorbuyer).document(userId.name)
            .collection(Constants.following).document(userToFollow.name).set(userToFollow).await()
    }

    suspend fun removeFromFollowers(userIdOrName: String, userToUnfollow: String): Void? {
        return cloud.collection(Constants.sellerorbuyer).document(userToUnfollow)
            .collection(Constants.followers).document(userIdOrName).delete().await()
    }

    suspend fun removeFromFollowing(userIdOrName: String, userToUnfollow: String): Void? {
        return cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
            .collection(Constants.following).document(userToUnfollow).delete().await()
    }

    suspend fun getFollowedUserId(userId: String, userToFollow: String): DocumentSnapshot? {
        return cloud.collection(Constants.sellerorbuyer).document(userId)
            .collection(Constants.following).document(userToFollow).get().await()
    }

    suspend fun getCar(brand: String, id: String): DocumentSnapshot? {
        return cloud.collection(Constants.car).document(brand)
            .collection(Constants.models).document(id).get().await()
    }

    suspend fun addCarToSellerList(product: Product, userId: String): Void? {
        val productId = ProductId(product.id)
        return cloud.collection(Constants.sellerorbuyer).document(userId)
            .collection(product.brand).document(product.id).set(productId).await()
    }

    suspend fun addCarToDb(product: Product): Void? {
        return cloud.collection(Constants.car).document(product.brand)
            .collection(Constants.models).document(product.id).set(product).await()
    }

    suspend fun setBrandDetails(product: Product): Void? {
        val modelName = ProductId(product.brand)
        return cloud.collection(Constants.car).document(product.brand)
            .set(modelName).await()
    }

    suspend fun getFeedbacksForCar(brand: String, id: String): QuerySnapshot? {
        return cloud.collection(Constants.car).document(brand)
            .collection(Constants.models).document(id)
            .collection(Constants.feedback).get()
            .await()
    }

    suspend fun addFeedbackForCar(brand: String, feedback: Feedback): DocumentReference? {
        return cloud.collection(Constants.car).document(brand)
            .collection(Constants.models).document(feedback.productId)
            .collection(Constants.feedback).add(feedback).await()
    }

    suspend fun reportProduct(report: Report, product: Product): DocumentReference? {
        cloud.collection(Constants.report).document(report.id).set(report).await()
       return cloud.collection(Constants.report).document(report.id).collection(product.brand)
            .add(product).await()
    }
}