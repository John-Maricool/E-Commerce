package com.maricoolsapps.e_commerce.data.interfaces

interface MapperInterface<CacheModel, ServerModel> {

    fun mapToCache(model: ServerModel): CacheModel
    fun mapAllToCache(model: List<ServerModel>): List<CacheModel>
}