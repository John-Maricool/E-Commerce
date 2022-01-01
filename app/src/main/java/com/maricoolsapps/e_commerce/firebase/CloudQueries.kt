package com.maricoolsapps.e_commerce.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Resource
import javax.inject.Inject

class CloudQueries
@Inject constructor(val cloud: FirebaseFirestore){

    fun getAllCarBrands():LiveData<Resource<List<String>>>{
        /*
        This function is responsible for getting all the
        brands of cars in the database, It places tbe names in the
        Tab layout for selection
         */
        val carBrands = MutableLiveData<Resource<List<String>>>()

        cloud.collection(Constants.car).get().addOnSuccessListener {
            val brands = mutableListOf<String>()
             it.documents.forEach {
                 brands.add(it.reference.id)
             }
            carBrands.value = Resource.success(brands)
         }.addOnFailureListener{
             Resource.error(it.message!!, null)
        }
        return carBrands
    }

    fun getCarsFromBrand(name: String): LiveData<Resource<List<Product>>>{
        /*
        This function is responsible for getting all the
        cars from a brand in the database, It places tbe cars in the
        recycler view for selection
         */
        val carsFromDb = MutableLiveData<Resource<List<Product>>>()

        cloud.collection(Constants.car).document(name).collection(Constants.models)
            .limit(4)
            .get(Source.DEFAULT)
            .addOnSuccessListener {
            val cars: List<Product> = it.toObjects(Product::class.java)
            carsFromDb.value = Resource.success(cars)
        }.addOnFailureListener{
            carsFromDb.value = Resource.error(it.message!!, null)
        }
        return carsFromDb
    }
}