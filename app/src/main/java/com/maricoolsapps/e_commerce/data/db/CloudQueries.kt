package com.maricoolsapps.e_commerce.data.db

import com.maricoolsapps.e_commerce.data.source.FirebaseFirestoreSource
import com.maricoolsapps.e_commerce.data.model.*
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.MapperImpl
import com.maricoolsapps.e_commerce.utils.Resource
import java.lang.Exception
import javax.inject.Inject

class CloudQueries
@Inject constructor(
    private var source: FirebaseFirestoreSource
) {

    suspend fun getCarsFromBrand(name: String, b: (Resource<List<ProductModel>>) -> Unit) {
        /*
        This function is responsible for getting all the
        cars from a brand in the database, It places tbe cars in the
        recycler view for selection
         */
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
        b.invoke(Resource.loading())
        try {
            val result = source.getUser(name)?.toObject(CarBuyerOrSeller::class.java)
            b.invoke(Resource.success(result))
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    suspend fun getNumberOfFollowers(userIdOrName: String, b: (Resource<Int>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            val result = source.getFollowersId(userIdOrName)?.documents?.size
            b.invoke(Resource.success(result))
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    suspend fun getNumberOfFollowing(userIdOrName: String, b: (Resource<Int>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            val result = source.getFollowingId(userIdOrName)?.documents?.size
            b.invoke(Resource.success(result))
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    suspend fun changeProfile(id: String, person: CarBuyerOrSeller, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.changeProfile(id, person)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.message.toString(), null))
        }
    }

    suspend fun removeProduct(
        name: String,
        brand: String,
        id: List<String>,
        b: (Resource<String>) -> Unit
    ) {
        b.invoke(Resource.loading())
        try {
            source.removeUserProducts(name, brand, id)
            source.removeProducts(brand, id)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.message.toString(), null))
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
        b.invoke(Resource.loading())
        try {
            val result = source.getCar(brand, id)?.toObject(Product::class.java)
            b.invoke(Resource.success(result))
        } catch (e: Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

    suspend fun addCarToDb(product: Product, userIdOrName: String, b: (Resource<String>) -> Unit) {
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
}