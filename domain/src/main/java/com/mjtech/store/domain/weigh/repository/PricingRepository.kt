package com.mjtech.store.domain.weigh.repository

import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.weigh.model.PriceSetting
import kotlinx.coroutines.flow.Flow

interface PricingRepository {

    fun getPriceSetting(): Flow<Result<PriceSetting>>
}