package com.mjtech.store.ui.weigh

import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.weigh.model.PriceSetting

data class WeighUiState(
    val price: Result<PriceSetting> = Result.Loading,
    val weight: Result<Double> = Result.Loading,
    val initialize: Result<Unit> = Result.Loading,
    val configScale: Result<Unit> = Result.Loading,
    val finish: Result<Unit> = Result.Loading,
    val activate: Result<Unit> = Result.Loading,
    val deactivate: Result<Unit> = Result.Loading,
    val addToCart: Result<Unit>? = null,
    val totalValue: Double = 0.0,
    val calculationError: String? = null
)