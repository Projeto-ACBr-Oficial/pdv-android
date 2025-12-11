package com.mjtech.store.domain.cart.repostitory

import com.mjtech.store.domain.cart.model.CartItem
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.products.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun addItem(product: Product): Flow<Result<Unit>>
    fun removeItem(product: Product): Flow<Result<Unit>>
    fun clearCart(): Flow<Result<Unit>>
    fun getCartItems(): Flow<Result<List<CartItem>>>
    fun getTotalPrice(): Flow<Result<Double>>
    fun getQuantityForProduct(productId: String): Flow<Result<Int>>
}