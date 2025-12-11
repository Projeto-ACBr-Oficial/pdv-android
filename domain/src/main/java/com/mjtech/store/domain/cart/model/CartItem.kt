package com.mjtech.store.domain.cart.model

import com.mjtech.store.domain.products.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val product: Product,
    val quantity: Int
)