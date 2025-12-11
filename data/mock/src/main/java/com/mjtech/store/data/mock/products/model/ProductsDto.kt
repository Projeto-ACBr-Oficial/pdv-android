package com.mjtech.store.data.mock.products.model

import com.mjtech.store.domain.products.model.Product
import kotlinx.serialization.Serializable

@Serializable
internal data class ProductsDto(
    val products: List<Product>
)