package com.maricoolsapps.e_commerce.data.db

import android.net.Uri
import com.maricoolsapps.e_commerce.data.source.FirebaseStorageSource
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Resource
import javax.inject.Inject

class StorageChanges
@Inject constructor(var source: FirebaseStorageSource) {

    suspend fun getDownloadUri(id: String, b: (Resource<Uri>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            val data = source.getDownloadUri(id)
            b.invoke(Resource.success(data))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun putFileInStorage(id: String, uri: Uri, b: (Resource<String>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            source.putFileInStorage(uri, id)
            b.invoke(Resource.success(Constants.successful))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }

    suspend fun getAllImageDownloadUri(images: List<Uri>, b: (Resource<List<String>>) -> Unit) {
        b.invoke(Resource.loading())
        try {
            val productImages = mutableListOf<String>()
            images.forEach {
                source.putFileInStorage(it, it.toString())
                val imgUrl = source.getDownloadUri(it.toString())
                productImages.add(imgUrl.toString())
            }
            b.invoke(Resource.success(productImages))
        } catch (e: java.lang.Exception) {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }

}