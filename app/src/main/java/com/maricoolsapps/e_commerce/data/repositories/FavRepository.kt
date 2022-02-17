package com.maricoolsapps.e_commerce.data.repositories

import com.maricoolsapps.e_commerce.data.repositories.CloudQueries
import com.maricoolsapps.e_commerce.data.model.Product
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