package com.maricoolsapps.e_commerce.firebase

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.model.Follow
import com.maricoolsapps.e_commerce.model.ProductId
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class CloudQueries
@Inject constructor(private val cloud: FirebaseFirestore) {

    fun getCarsFromBrand(name: String): LiveData<Resource<List<Product>>> {
        /*
        This function is responsible for getting all the
        cars from a brand in the database, It places tbe cars in the
        recycler view for selection
         */
        val carsFromDb = MutableLiveData<Resource<List<Product>>>()

        cloud.collection(Constants.car).document(name).collection(Constants.models)
            .get(Source.SERVER)
            .addOnSuccessListener {
                val cars: List<Product> = it.toObjects(Product::class.java)
                carsFromDb.value = Resource.success(cars)
            }.addOnFailureListener {
                carsFromDb.value = Resource.error(it.message!!, null)
            }
        return carsFromDb
    }

    fun getCarsFromSeller(name: String, brand: String): LiveData<Resource<List<Product>>> {
        /*
        * This function is used to get all the cars from a particular seller
        * */
        val sellerCars = mutableListOf<Product>()
        val done = MutableLiveData<Resource<List<Product>>>()

        cloud.collection(Constants.sellerorbuyer).document(name)
            .collection(brand).get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()) {
                    it.documents.forEach { snap ->
                        val ref = snap.reference.id
                        cloud.collection(Constants.car).document(brand)
                            .collection(Constants.models)
                            .document(ref).get().addOnSuccessListener { snapshot ->
                                val product = snapshot.toObject(Product::class.java)
                                sellerCars.add(product!!)
                                done.value = Resource.success(sellerCars)
                            }.addOnFailureListener {
                                done.value = Resource.error(it.message.toString(), null)
                            }
                    }

                    done.value = Resource.success(sellerCars)
                }
            }.addOnFailureListener {
                done.value = Resource.error(it.message.toString(), null)
            }
        return done
    }

    fun getSeller(name: String): LiveData<Resource<CarBuyerOrSeller>> {
        /*
        * This function makes it possible to get the seller of the product.
        * */
        val seller = MutableLiveData<Resource<CarBuyerOrSeller>>()
        cloud.collection(Constants.sellerorbuyer)
            .document(name).get()
            .addOnSuccessListener {
                seller.value = Resource.success(it.toObject(CarBuyerOrSeller::class.java))
            }.addOnFailureListener {
                seller.value = Resource.error(it.message.toString(), null)
            }
        return seller
    }

    fun getNumberOfFollowers(userIdOrName: String): LiveData<Resource<Int>> {

        val done = MutableLiveData<Resource<Int>>()
        cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
            .collection(Constants.followers)
            .get().addOnSuccessListener {
                done.value = Resource.success(it.documents.size)
            }.addOnFailureListener {
                done.value = Resource.error(it.message.toString(), 0)
            }
        return done
    }

    suspend fun changeProfile(id: String, person: CarBuyerOrSeller): Resource<String> {
        val userimage = person.image?.toUri()

        if (userimage.toString().take(4) == "http"){
            val imgUrl = FirebaseStorage.getInstance()
                .getReference("${id}.jpg")
                .downloadUrl.await()
            person.image = imgUrl.toString()
        }else{
            val image = FirebaseStorage.getInstance()
                .getReference("${id}.jpg")
                .putFile(userimage!!)
            image.await()
             if (image.isComplete) {
                val imgUrl = FirebaseStorage.getInstance()
                    .getReference("${id}.jpg")
                    .downloadUrl.await()
                person.image = imgUrl.toString()
            }else{
                Resource.error("Error", null)
            }
        }
        return try {
            cloud.collection(Constants.sellerorbuyer)
                .document(id).set(person).await()
            Resource.success("Successful")
        } catch (e: Exception) {
            Resource.error(e.message.toString(), null)
        }
    }

    suspend fun removeProduct(name: String, brand: String, id: List<String>): Resource<String>{
       return try {
           id.forEach { it ->
               cloud.collection(Constants.sellerorbuyer).document(name)
                   .collection(brand).document(it).delete().await()
               cloud.collection(Constants.car).document(brand)
                   .collection(Constants.models).document(it).delete().await()
           }
            Resource.success("Successfully deleted")
        }catch (e: Exception){
            Resource.error(e.message.toString(), null)
        }
    }

    fun getFollowers(userIdOrName: String): LiveData<Resource<List<CarBuyerOrSeller>?>>{
        val followers = mutableListOf<CarBuyerOrSeller?>()
        val result = MutableLiveData<Resource<List<CarBuyerOrSeller>?>>()
        cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
            .collection(Constants.followers).get()
            .addOnSuccessListener {query ->
                query.documents.forEach { documentSnapshot ->
                    documentSnapshot.reference.get().addOnSuccessListener {
                        followers.add(it.toObject(CarBuyerOrSeller::class.java))
                    }.addOnFailureListener{
                        result.postValue(Resource.error(it.message.toString(), null))
                    }
                }
                result.postValue(Resource.success(followers.toList()) as Resource<List<CarBuyerOrSeller>>?)
            }.addOnFailureListener {
                result.postValue(Resource.error(it.message.toString(), null))
            }
        return result
    }

    fun getFollowing(userIdOrName: String): LiveData<Resource<List<CarBuyerOrSeller>?>>{
        val followers = mutableListOf<CarBuyerOrSeller?>()
        val result = MutableLiveData<Resource<List<CarBuyerOrSeller>?>>()
        cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
            .collection(Constants.following).get()
            .addOnSuccessListener {query ->
                query.documents.forEach { documentSnapshot ->
                    documentSnapshot.reference.get().addOnSuccessListener {
                        followers.add(it.toObject(CarBuyerOrSeller::class.java))
                    }.addOnFailureListener{
                        result.postValue(Resource.error(it.message.toString(), null))
                    }
                }
                result.postValue(Resource.success(followers.toList()) as Resource<List<CarBuyerOrSeller>>?)
            }.addOnFailureListener {
                result.postValue(Resource.error(it.message.toString(), null))
            }
        return result
    }

    fun getNumberOfFollowing(userIdOrName: String): LiveData<Resource<Int>> {

        val done = MutableLiveData<Resource<Int>>()
        cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
            .collection(Constants.following)
            .get().addOnSuccessListener {
                done.value = Resource.success(it.documents.size)
            }.addOnFailureListener {
                done.value = Resource.error(it.message.toString(), 0)
            }
        return done
    }

    suspend fun followUser(userIdOrName: String, userToFollow: String): Resource<String> {

        val fol = Follow(userToFollow)
        val foll = Follow(userIdOrName)
        return try {
            withContext(Dispatchers.Main) {
                cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
                    .collection(Constants.following)
                    .document(userToFollow).set(fol)
                cloud.collection(Constants.sellerorbuyer).document(userToFollow)
                    .collection(Constants.followers)
                    .document(userIdOrName).set(foll)
            }
            Resource.success("Followed user...")
        } catch (e: Exception) {
            Resource.error("Error following user", null)
        }
    }

    suspend fun unfollowUser(userIdOrName: String, userToUnfollow: String): Resource<String> {

        return try {
            withContext(Dispatchers.IO) {
                cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
                    .collection(Constants.following)
                    .document(userToUnfollow).delete()
                cloud.collection(Constants.sellerorbuyer).document(userToUnfollow)
                    .collection(Constants.followers)
                    .document(userIdOrName)
                    .delete()
            }
            Resource.success("Followed user")
        } catch (e: Exception) {
            Resource.error("Error following user", null)
        }
    }

    fun isUserFollowed(userId: String, userToFollow: String): LiveData<Resource<Boolean>> {
        val isFollowed = MutableLiveData<Resource<Boolean>>()
        cloud.collection(Constants.sellerorbuyer).document(userId)
            .collection(Constants.following)
            .document(userToFollow).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    isFollowed.value = Resource.success(true)
                } else {
                    isFollowed.value = Resource.error("You are not following this user", false)
                }
            }.addOnFailureListener {
                isFollowed.value = Resource.error("You are not following this user", false)
            }
        return isFollowed
    }

    suspend fun getCar(brand: String, id: String): Product? {
        return cloud.collection(Constants.car).document(brand)
            .collection(Constants.models)
            .document(id)
            .get()
            .await().toObject(Product::class.java)
    }

    suspend fun getAllImageDownloadUri(images: List<Uri>): List<String> {
        val productImages = mutableListOf<String>()
        images.forEach {
            val image = FirebaseStorage.getInstance().getReference("$it.jpg").putFile(it)
            image.await()
            if (image.isComplete) {
                val imgUrl = FirebaseStorage.getInstance().getReference("${it}.jpg").downloadUrl.await()
                productImages.add(imgUrl.toString())
            }
        }
        return productImages
    }

    suspend fun addCarToDb(product: Product, userIdOrName: String): Resource<String> {
        val productID = ProductId(product.id)
        val productCar = cloud.collection(Constants.car).document(product.brand)
            .collection(Constants.models).document(product.id).set(product)
        productCar.await()
        return if (productCar.isSuccessful) {
            val sellerCar = cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
                .collection(product.brand).document(product.id).set(productID)
            sellerCar.await()
            if (sellerCar.isSuccessful) {
                Resource.success("Successful")
            }else{
                Resource.error("Error", null)
            }
        }else{
            Resource.error("Error", null)
        }
    }
}