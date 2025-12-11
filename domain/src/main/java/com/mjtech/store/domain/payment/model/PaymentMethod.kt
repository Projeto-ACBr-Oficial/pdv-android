package com.mjtech.store.domain.payment.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethod(
    val id: String,
    val name: String,
    val maxInstallments: Int = 1,
    val requiresInstallments: Boolean = false
)
