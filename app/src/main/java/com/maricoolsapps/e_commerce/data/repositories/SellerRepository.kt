package com.maricoolsapps.e_commerce.data.repositories

import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.CarSellerProfile
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status
import javax.inject.Inject

class SellerRepository
@Inject constructor(val cloud: CloudQueries) {

    suspend fun sellerProfile(
        userId: String,
        userToFollow: String,
        b: (Resource<CarSellerProfile>) -> Unit
    ) {
        b.invoke(Resource.loading())
        val sellerProfile = CarSellerProfile(null, null)
        try {
            cloud.getSeller(userToFollow) {
                when (it.status) {
                    Status.SUCCESS -> sellerProfile.seller = it.data
                    else -> sellerProfile.seller = null
                }
            }
            cloud.isUserFollowed(userId, userToFollow) {
                when(it.status) {
                    Status.SUCCESS -> sellerProfile.isFollowing = it.data
                    else -> sellerProfile.isFollowing = false
                }
            }
            b.invoke(Resource.success(sellerProfile))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

}