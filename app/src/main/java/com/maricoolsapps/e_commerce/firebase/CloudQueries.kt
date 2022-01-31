package com.maricoolsapps.e_commerce.firebase

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.maricoolsapps.e_commerce.model.*
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.MapperImpl
import com.maricoolsapps.e_commerce.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class CloudQueries
@Inject constructor(private val cloud: FirebaseFirestore) {

    fun getCarsFromBrand(name: String): LiveData<Resource<List<ProductModel>>> {
        /*
        This function is responsible for getting all the
        cars from a brand in the database, It places tbe cars in the
        recycler view for selection
         */
        val carsFromDb = MutableLiveData<Resource<List<ProductModel>>>()

        cloud.collection(Constants.car).document(name).collection(Constants.models)
            .get(Source.SERVER)
            .addOnSuccessListener {
                val cars: List<Product> = it.toObjects(Product::class.java)
                val mapper = MapperImpl.mapAllToCache(cars)
                if (cars.isEmpty()){
                    carsFromDb.value = Resource.error(Constants.no_data, null)
                }else{
                    carsFromDb.value = Resource.success(mapper)
                }
            }.addOnFailureListener {
                carsFromDb.value = Resource.error(Constants.check_internet, null)
            }
        return carsFromDb
    }

    fun getCarsFromSeller(name: String, brand: String): LiveData<Resource<List<ProductModel>>> {
        /*
        * This function is used to get all the cars from a particular seller
        * */
        val sellerCars = mutableListOf<ProductModel>()
        val done = MutableLiveData<Resource<List<ProductModel>>>()

        cloud.collection(Constants.sellerorbuyer).document(name)
            .collection(brand).get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()) {
                    it.documents.forEach { snap ->
                        val ref = snap.reference.id
                        cloud.collection(Constants.car).document(brand)
                            .collection(Constants.models)
                            .document(ref).get().addOnSuccessListener { snapshot ->
                                val product = snapshot.toObject(ProductModel::class.java)
                                sellerCars.add(product!!)
                                done.value = Resource.success(sellerCars)
                            }.addOnFailureListener {
                                done.value = Resource.error("Check your internet connection", null)
                            }
                    }
                    done.value = Resource.success(sellerCars)
                } else {
                    done.value = Resource.error("You have not uploaded any data", null)
                }
            }.addOnFailureListener {
                done.value = Resource.error("Check your internet connection", null)
            }
        return done
    }

    suspend fun getSeller(name: String): Resource<CarBuyerOrSeller> {
        /*
        * This function makes it possible to get the seller of the product.
        * */
        return try {
            val result = cloud.collection(Constants.sellerorbuyer)
                .document(name).get().await().toObject(CarBuyerOrSeller::class.java)
            Resource.success(result)
        } catch (e: Exception) {
            Resource.error(Constants.check_internet, null)
        }
    }

    suspend fun getNumberOfFollowers(userIdOrName: String): Resource<Int> {
        return try{
            val result = cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
                .collection(Constants.followers)
                .get().await().documents.size
            Resource.success(result)
        }catch (e: Exception) {
            Resource.error(Constants.check_internet, 0)
        }
    }

    suspend fun getNumberOfFollowing(userIdOrName: String): Resource<Int> {
        return try{
            val result = cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
                .collection(Constants.following)
                .get().await().documents.size
            Resource.success(result)
        }catch (e: Exception) {
            Resource.error(Constants.check_internet, 0)
        }
    }

    suspend fun changeProfile(id: String, person: CarBuyerOrSeller): Resource<String> {
        val userimage = person.image?.toUri()

        if (userimage.toString().take(4) == "http") {
            val imgUrl = FirebaseStorage.getInstance()
                .getReference("${id}.jpg")
                .downloadUrl.await()
            person.image = imgUrl.toString()
        } else {
            val image = FirebaseStorage.getInstance()
                .getReference("${id}.jpg")
                .putFile(userimage!!)
            image.await()
            if (image.isComplete) {
                val imgUrl = FirebaseStorage.getInstance()
                    .getReference("${id}.jpg")
                    .downloadUrl.await()
                person.image = imgUrl.toString()
            } else {
                Resource.error(Constants.error, null)
            }
        }
        return try {
            cloud.collection(Constants.sellerorbuyer)
                .document(id).set(person).await()
            Resource.success(Constants.successful)
        } catch (e: Exception) {
            Resource.error(e.message.toString(), null)
        }
    }

    suspend fun removeProduct(name: String, brand: String, id: List<String>): Resource<String> {
        return try {
            id.forEach { it ->
                cloud.collection(Constants.sellerorbuyer).document(name)
                    .collection(brand).document(it).delete().await()
                cloud.collection(Constants.car).document(brand)
                    .collection(Constants.models).document(it).delete().await()
            }
            Resource.success(Constants.successful)
        } catch (e: Exception) {
            Resource.error(e.message.toString(), null)
        }
    }

    fun getFollowers(userIdOrName: String): LiveData<Resource<List<CarBuyerOrSeller>?>> {
        val followers = mutableListOf<CarBuyerOrSeller?>()
        val result = MutableLiveData<Resource<List<CarBuyerOrSeller>?>>()
        cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
            .collection(Constants.followers)
            .get()
            .addOnSuccessListener {query ->
                if (query.documents.isEmpty()){
                    result.postValue(Resource.error(Constants.no_followers, null))
                }else {
                    query.documents.forEach { documentSnapshot ->
                        cloud.collection(Constants.sellerorbuyer)
                            .document(documentSnapshot.reference.id)
                            .get().addOnSuccessListener {
                                followers.add(it.toObject(CarBuyerOrSeller::class.java))
                                Log.d("dff", followers.toString())
                            }.addOnFailureListener {
                                result.postValue(Resource.error(it.message.toString(), null))
                            }
                    }
                }
                result.postValue(Resource.success(followers.toList()) as Resource<List<CarBuyerOrSeller>>?)
            }.addOnFailureListener {
                result.postValue(Resource.error(it.message.toString(), null))
            }
        return result
    }

    fun getFollowing(userIdOrName: String): LiveData<Resource<List<CarBuyerOrSeller>?>> {
        val followers = mutableListOf<CarBuyerOrSeller?>()
        val result = MutableLiveData<Resource<List<CarBuyerOrSeller>?>>()
        cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
            .collection(Constants.following).get()
            .addOnSuccessListener { query ->
                if (query.documents.isEmpty()){
                    result.postValue(Resource.error(Constants.no_following, null))
                }else {
                    query.documents.forEach { documentSnapshot ->
                        cloud.collection(Constants.sellerorbuyer)
                            .document(documentSnapshot.reference.id)
                            .get().addOnSuccessListener {
                                followers.add(it.toObject(CarBuyerOrSeller::class.java))
                                Log.d("dff", followers.toString())
                            }.addOnFailureListener {
                                result.postValue(Resource.error(it.message.toString(), null))
                            }
                    }
                }
                result.postValue(Resource.success(followers.toList()) as Resource<List<CarBuyerOrSeller>>?)
            }.addOnFailureListener {
                result.postValue(Resource.error(it.message.toString(), null))
            }
        return result
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
            Resource.success(Constants.successful)
        } catch (e: Exception) {
            Resource.error(Constants.error, null)
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
            Resource.success(Constants.successful)
        } catch (e: Exception) {
            Resource.error(Constants.error, null)
        }
    }

    suspend fun isUserFollowed(userId: String, userToFollow: String): Resource<Boolean> {
        return try {
            val following = cloud.collection(Constants.sellerorbuyer).document(userId)
                .collection(Constants.following)
                .document(userToFollow).get().await()
            if (following.exists()) {
                Resource.success(true)
            } else {
                Resource.error(Constants.error, false)
            }
        } catch (e: Exception) {
            Resource.error(Constants.check_internet, false)
        }
    }

    suspend fun getCar(brand: String, id: String): Resource<Product> {
        return try {
            val result = cloud.collection(Constants.car).document(brand)
                .collection(Constants.models)
                .document(id)
                .get()
                .await().toObject(Product::class.java)
            Resource.success(result)
        } catch (e: Exception) {
            Resource.error(Constants.check_internet, null)
        }
    }

    suspend fun getAllImageDownloadUri(images: List<Uri>): List<String> {
        val productImages = mutableListOf<String>()
        images.forEach {
            val image = FirebaseStorage.getInstance().getReference("$it.jpg").putFile(it)
            image.await()
            if (image.isComplete) {
                val imgUrl =
                    FirebaseStorage.getInstance().getReference("${it}.jpg").downloadUrl.await()
                productImages.add(imgUrl.toString())
            }
        }
        return productImages
    }

    suspend fun addCarToDb(product: Product, userIdOrName: String): Resource<String> {
        val productID = ProductId(product.id)
        val modelName = ProductId(product.brand)
        cloud.collection(Constants.car).document(product.brand).set(modelName).await()
        val productCar = cloud.collection(Constants.car).document(product.brand)
            .collection(Constants.models).document(product.id).set(product)
        productCar.await()
        return if (productCar.isSuccessful) {
            val sellerCar = cloud.collection(Constants.sellerorbuyer).document(userIdOrName)
                .collection(product.brand).document(product.id).set(productID)
            sellerCar.await()
            if (sellerCar.isSuccessful) {
                Resource.success(Constants.successful)
            } else {
                Resource.error(Constants.error, null)
            }
        } else {
            Resource.error(Constants.error,null)
        }
    }
}