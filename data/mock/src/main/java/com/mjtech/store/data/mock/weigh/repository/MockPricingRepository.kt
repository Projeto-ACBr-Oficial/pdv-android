package com.mjtech.store.data.mock.weigh.repository

import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.weigh.model.PriceSetting
import com.mjtech.store.domain.weigh.repository.PricingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class MockPricingRepository : PricingRepository {

    private val currentPrice = MutableStateFlow(
        PriceSetting(
            pricePerKg = 80.00,
            unit = "KG"
        )
    )

    override fun getPriceSetting(): Flow<Result<PriceSetting>> = flow {
        emit(Result.Loading)
        try {
            val priceSetting = currentPrice.value
            emit(Result.Success(priceSetting))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao obter configuração de preço: ${e.message}"))
        }
    }
}