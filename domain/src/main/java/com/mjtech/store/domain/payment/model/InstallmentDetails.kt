package com.mjtech.store.domain.payment.model

import kotlinx.serialization.Serializable

/**
 * @param installments Quantidade de parcelas
 * @param installmentType Tipo de parcelamento
 */
@Serializable
data class InstallmentDetails(
    val installments: Int,
    val installmentType: InstallmentType
)