package com.maricoolsapps.e_commerce.repos

import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import com.maricoolsapps.e_commerce.room_db.ProductDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductDetailRepo
@Inject constructor(val dao: ProductDao, val scope: CoroutineScope){

     fun insertCarToDb(productEntity: FavoriteProductEntity){
         scope.launch (IO){
             dao.addCar(productEntity)
         }
    }

    fun deleteCar(carId: String){
        scope.launch (IO){
            dao.deleteCar(carId)
        }
    }

     fun isCarAddedToFav(id: String): Boolean {
        return dao.checkIfAdded(id)
    }
}