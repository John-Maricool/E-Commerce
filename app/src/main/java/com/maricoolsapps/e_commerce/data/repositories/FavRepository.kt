package com.maricoolsapps.e_commerce.data.repositories

import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.source.FirebaseFirestoreSource
import com.maricoolsapps.e_commerce.room_db.ProductDao
import com.maricoolsapps.e_commerce.utils.MapperImpl
import com.maricoolsapps.e_commerce.utils.Resource
import java.lang.Exception
import javax.inject.Inject

class FavRepository
@Inject constructor(val cloud: FirebaseFirestoreSource, val dao: ProductDao) {

    suspend fun getAllFavoriteCars(b: (Resource<List<ProductModel>>) -> Unit) {
        b.invoke(Resource.loading())
        val products = mutableListOf<ProductModel>()

        try {
            val allcars = dao.getFavs()
            allcars.forEach { favProd ->
                cloud.getCar(favProd.brand, favProd.id)
                val res = MapperImpl.mapToCache(
                    cloud.getCar(favProd.brand, favProd.id)?.toObject(Product::class.java)!!
                )
                products.add(res)
            }
            b.invoke(Resource.success(products.toList()))
        } catch (e: Exception) {
            b.invoke(Resource.error(e.toString(), null))
        }
    }
}