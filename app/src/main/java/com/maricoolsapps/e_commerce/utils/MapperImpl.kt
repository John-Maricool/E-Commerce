package com.maricoolsapps.e_commerce.utils

import com.maricoolsapps.e_commerce.interfaces.MapperInterface
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.ProductModel

object MapperImpl: MapperInterface<ProductModel, Product> {

    override fun mapToCache(model: Product): ProductModel {
      return ProductModel(model.type,
      model.description,
          model.state,
          model.town,
          model.id,
          model.condition,
          model.brand,
          model.photos[0],
          model.price
      )
    }

    override fun mapAllToCache(model: List<Product>): List<ProductModel> {
        val result = mutableListOf<ProductModel>()
         model.forEach {
             result.add(mapToCache(it))
        }
        return result
    }
}