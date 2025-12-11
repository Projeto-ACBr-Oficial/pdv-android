package com.mjtech.store.domain.payment.model

import kotlinx.serialization.Serializable

@Serializable
data class InstallmentOption(
    val count: Int,
    val totalAmount: Double,
    val interestRate: Double = 0.0,
    val monthlyAmount: Double
)
