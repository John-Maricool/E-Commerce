package com.maricoolsapps.e_commerce.repos

import android.util.Log
import androidx.lifecycle.LiveData
import com.maricoolsapps.e_commerce.firebase.CloudQueries
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.ProductModel
import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import com.maricoolsapps.e_commerce.room_db.ProductDao
import com.maricoolsapps.e_commerce.utils.Resource
import javax.inject.Inject

class FavRepository
@Inject constructor(val cloud: CloudQueries, val dao: ProductDao){

     suspend fun getAllFavCarsFromDb(): List<FavoriteProductEntity> {
         return dao.getFavs()
    }

    suspend fun getCar(productEntity: FavoriteProductEntity): Resource<Product> {
            return cloud.getCar(productEntity.brand, productEntity.id)
    }
}