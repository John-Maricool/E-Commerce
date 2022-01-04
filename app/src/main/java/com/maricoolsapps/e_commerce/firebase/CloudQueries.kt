package com.maricoolsapps.e_commerce.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Resource
import javax.inject.Inject

class CloudQueries
@Inject constructor(val cloud: FirebaseFirestore) {

    fun getAllCarBrands(): LiveData<Resource<List<String>>> {
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
        }.addOnFailureListener {
            Resource.error(it.message!!, null)
        }
        return carBrands
    }

    fun getCarsFromBrand(name: String): LiveData<Resource<List<Product>>> {
        /*
        This function is responsible for getting all the
        cars from a brand in the database, It places tbe cars in the
        recycler view for selection
         */
        val carsFromDb = MutableLiveData<Resource<List<Product>>>()

        cloud.collection(Constants.car).document(name).collection(Constants.models)
            .get()
            .addOnSuccessListener {
                val cars: List<Product> = it.toObjects(Product::class.java)
                carsFromDb.value = Resource.success(cars)
            }.addOnFailureListener {
                carsFromDb.value = Resource.error(it.message!!, null)
            }
        return carsFromDb
    }

    fun getCarsFromSeller(name: String, brand: String): LiveData<Resource<List<Product>>> {
        /*
        * This function is used to get all the cars from a particular seller
        * */
        val carsFromDb = MutableLiveData<Resource<List<Product>>>()

        cloud.collection(Constants.sellerorbuyer).document(name).collection(brand).get()
            .addOnSuccessListener {query ->
                val sellerCars = mutableListOf<Product>()
                query.documents.forEach {snapshot ->
                    val ref = snapshot.reference.id
                    cloud.collection(Constants.car).document(brand).collection(Constants.models)
                        .document(ref).get().addOnSuccessListener {docSnapshot ->
                            val product = docSnapshot.toObject(Product::class.java)
                            sellerCars.add(product!!)
                        }
                }
                carsFromDb.value = Resource.success(sellerCars)
            }.addOnFailureListener {
                Resource.error(it.message!!, null)
            }
        return carsFromDb
    }

    fun getSeller(name: String): LiveData<Resource<CarBuyerOrSeller>>{
        val sellerId = MutableLiveData<Resource<CarBuyerOrSeller>>()
        /*
        * This function makes it possible to get the seller of the product.
        * */
        cloud.collection(Constants.sellerorbuyer).document(name).get().addOnSuccessListener {
            val seller = it.toObject(CarBuyerOrSeller::class.java)
            sellerId.value = Resource.success(seller)
        }.addOnFailureListener{
            Resource.error(it.message!!, null)
        }
        return sellerId
    }
}