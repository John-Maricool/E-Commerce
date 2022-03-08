package com.maricoolsapps.e_commerce.data.repositories

import android.content.Context
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.source.FirebaseFirestoreSource
import com.maricoolsapps.e_commerce.room_db.ProductDao
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.MapperImpl
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.checkForInternet
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import javax.inject.Inject
/*

class FavRepository
@Inject constructor(val cloud: FirebaseFirestoreSource, val dao: ProductDao, @ApplicationContext val context: Context) {

    suspend fun getAllFavoriteCars(b: (Resource<List<ProductModel>>) -> Unit) {
        if (context.checkForInternet()) {
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
        } else {
            b.invoke(Resource.error(Constants.check_internet, null))
        }
    }
}*/
