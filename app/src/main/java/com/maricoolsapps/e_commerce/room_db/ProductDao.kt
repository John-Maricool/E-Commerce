package com.maricoolsapps.e_commerce.room_db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun addCar(car: FavoriteProductEntity)

    @Query("delete from FavoriteProductEntity where id = :carId")
    suspend fun deleteCar(carId: String)

    @Query("select * from FavoriteProductEntity")
    suspend fun getFavs(): List<FavoriteProductEntity>

    @Query("select exists(select * from FavoriteProductEntity where id =:carId)")
     fun checkIfAdded(carId: String): Boolean

}