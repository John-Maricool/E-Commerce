package com.maricoolsapps.e_commerce.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.FirebaseException
import com.maricoolsapps.e_commerce.data.db.CloudQueries
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status
import java.io.IOException


private const val STARTING_PAGE_INDEX = 1

class CarsPagingSource(
    private val cloud: CloudQueries,
    private val query: String
) : PagingSource<Int, ProductModel>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductModel> {

        val position = params.key ?: STARTING_PAGE_INDEX

        var photos = listOf<ProductModel>()
        return try {
            cloud.getCarsFromBrand(query) {
                photos = when (it.status) {
                    Status.SUCCESS -> {
                        it.data!!
                    }
                    else -> emptyList()
                }
            }

            LoadResult.Page(
                data = photos,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: FirebaseException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}