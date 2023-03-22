package com.maricoolsapps.e_commerce.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.Follow
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowersStatus
@Inject constructor(val cloud: CloudQueries, val auth: FirebaseAuth, val scope: CoroutineScope) {

    fun isUserFollowed(name: String, b: (Resource<Boolean>) -> Unit) {
        scope.launch(Main) {
            cloud.isUserFollowed(auth.currentUser!!.uid, name) {
                b.invoke(it)
            }
        }
    }

    fun followUser(name: String, b: (Resource<String>) -> Unit) {
        val userId = Follow(auth.currentUser!!.uid)
        val userToFollow = Follow(name)
        scope.launch(Main) {
            cloud.followUser(userId, userToFollow) {
                b.invoke(it)
            }
        }
    }

    fun unfollowUser(name: String, b: (Resource<String>) -> Unit) {
        scope.launch(Main) {
            cloud.unfollowUser(auth.currentUser!!.uid, name) {
                b.invoke(it)
            }
        }
    }


}