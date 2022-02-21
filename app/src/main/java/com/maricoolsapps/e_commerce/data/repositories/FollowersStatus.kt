package com.maricoolsapps.e_commerce.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import javax.inject.Inject

class FollowersStatus
@Inject constructor(val cloud: CloudQueries, val auth: FirebaseAuth, val scope: CoroutineScope){
/*

    suspend fun isUserFollowed(name: String): Resource<Boolean> {
        val job = scope.async (IO){
            cloud.isUserFollowed(auth.currentUser!!.uid, name)
        }
        return job.await()
    }

    suspend fun followUser(name: String): Resource<String> {
        val job = scope.async (IO){
            cloud.followUser(auth.currentUser!!.uid, name)
        }
        return job.await()
    }

    suspend fun unfollowUser(name: String): Resource<String> {
        val job = scope.async (IO){
            cloud.unfollowUser(auth.currentUser!!.uid, name)
        }
        return job.await()
    }
*/

}