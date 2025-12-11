package com.mjtech.store.data.mock.cart.repository

import com.mjtech.store.domain.cart.model.CartItem
import com.mjtech.store.domain.cart.repostitory.CartRepository
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.products.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class MockCartRepository : CartRepository {

    private val _cartItemsFlow = MutableStateFlow<MutableMap<String, CartItem>>(mutableMapOf())

    override fun addItem(product: Product): Flow<Result<Unit>> =
        flow {
            emit(Result.Loading)
            try {
                val productId = product.id

                val updatedCart = _cartItemsFlow.value.toMutableMap()

                if (updatedCart.containsKey(productId)) {
                    val cartItem = updatedCart.getValue(productId)
                    updatedCart[productId] = cartItem.copy(quantity = cartItem.quantity + 1)
                } else {
                    updatedCart[productId] = CartItem(product = product, quantity = 1)
                }

                _cartItemsFlow.value = updatedCart

                emit(Result.Success(Unit))
            } catch (e: Exception) {
                emit(Result.Error("Erro ao adicionar item: ${e.message}"))
            }
        }

    override fun removeItem(product: Product): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val productId = product.id

            val updatedCart = _cartItemsFlow.value.toMutableMap()

            if (updatedCart.containsKey(productId)) {
                val cartItem = updatedCart.getValue(productId)

                if (cartItem.quantity > 1) {
                    updatedCart[productId] = cartItem.copy(quantity = cartItem.quantity - 1)
                } else {
                    updatedCart.remove(productId)
                }

                _cartItemsFlow.value = updatedCart
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error("Item não encontrado no carrinho"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Erro ao remover item: ${e.message}"))
        }
    }

    override fun clearCart(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            _cartItemsFlow.update { mutableMapOf() }
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao limpar o carrinho: ${e.message}"))
        }
    }

    override fun getCartItems(): Flow<Result<List<CartItem>>> =
        _cartItemsFlow
            .map { cartMap ->
                Result.Success(cartMap.values.toList()) as Result<List<CartItem>>
            }
            .catch { e ->
                emit(Result.Error("Erro ao obter itens do carrinho: ${e.message}"))
            }

    override fun getTotalPrice(): Flow<Result<Double>> =
        _cartItemsFlow
            .map { cartMap ->
                val totalPrice = cartMap.values.sumOf { item ->
                    item.product.price * item.quantity
                }
                Result.Success(totalPrice) as Result<Double>
            }
            .catch { e ->
                emit(Result.Error("Erro ao calcular o preço total: ${e.message}"))
            }

    override fun getQuantityForProduct(productId: String): Flow<Result<Int>> =
        _cartItemsFlow
            .map { cartMap ->
                val quantity = cartMap[productId]?.quantity ?: 0
                Result.Success(quantity) as Result<Int>
            }
            .catch { e ->
                emit(Result.Error("Erro ao obter quantidade do produto: ${e.message}"))
            }
}