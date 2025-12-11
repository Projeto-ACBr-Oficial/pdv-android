package com.mjtech.store.domain.products.repository

import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.products.model.Category
import com.mjtech.store.domain.products.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {

    fun getCategories(): Flow<Result<List<Category>>>
    fun getProducts(): Flow<Result<List<Product>>>
}