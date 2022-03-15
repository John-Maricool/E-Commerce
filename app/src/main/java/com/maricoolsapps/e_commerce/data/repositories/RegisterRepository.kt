package com.maricoolsapps.e_commerce.data.repositories

import android.content.SharedPreferences
import androidx.core.net.toUri
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.db.ProfileChanges
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.data.source.FirebaseStorageSource
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.SharedPrefs.USER_EMAIL
import com.maricoolsapps.e_commerce.utils.SharedPrefs.USER_NAME
import com.maricoolsapps.e_commerce.utils.SharedPrefs.USER_PIC
import javax.inject.Inject

class RegisterRepository
@Inject constructor(
    val auth: ProfileChanges,
    val storage: FirebaseStorageSource,
    val cloud: CloudQueries
) {

    suspend fun createNewUser(user: User, b: (Resource<String>) -> Unit) {
        auth.createNewUser(user) {
            b.invoke(it)
        }
    }

    fun addToken(token: String, b: (Resource<String>) -> Unit){
        cloud.addTokenToFirebase(auth.source.auth.currentUser?.uid!!, token){
            b.invoke(it)
        }
    }

    suspend fun changeProfile(id: String, person: CarBuyerOrSeller, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        val userimage = person.image?.toUri()


        if (userimage.toString().take(4) == "http") {
            val imgUrl = storage.getDownloadUri(id)
            person.image = imgUrl.toString()
        } else {
            storage.putFileInStorage(userimage!!, id)
            val imgUrl = storage.getDownloadUri(id)

            person.image = imgUrl.toString()
        }
        try {
            cloud.changeProfileNew(id, person) {
                b.invoke(it)
            }
        } catch (e: Exception) {
            b.invoke(Resource.error(e.message.toString(), null))
        }
    }
}