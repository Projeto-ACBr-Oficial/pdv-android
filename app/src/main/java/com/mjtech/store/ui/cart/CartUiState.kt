package com.mjtech.store.ui.cart

import com.mjtech.store.domain.common.Result

data class CartUiState(
    val totalItemsCount: Int = 0,
    val totalPrice: Double = 0.0,
    val quantitiesProducts: Map<String, Int> = emptyMap(),
    val addItemState: Result<Unit> = Result.Success(Unit),
    val removeItemState: Result<Unit> = Result.Success(Unit)
)