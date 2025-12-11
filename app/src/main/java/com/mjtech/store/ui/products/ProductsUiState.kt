package com.mjtech.store.ui.products

import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.products.model.Category
import com.mjtech.store.domain.products.model.Product

data class ProductsUiState(
    val categories: Result<List<Category>> = Result.Loading,
    val products: Result<List<Product>> = Result.Loading,
    val selectedCategoryId: String = "0",
    val searchQuery: String = ""
)