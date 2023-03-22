package com.maricoolsapps.e_commerce.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.source.CarsPagingSource
import javax.inject.Inject

class ProductistRepo
@Inject constructor(val cloudQueries: CloudQueries){

    fun getSearchResults(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 300,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CarsPagingSource(cloudQueries, query) }
        ).liveData
}