package com.mjtech.store.domain.payment.model

import kotlinx.serialization.Serializable

/**
 * @param id O ID do pagamento.
 * @param amount O valor do pagamento.
 * @param type O tipo de pagamento (crédito, débito, pix, etc.).
 * @param installmentDetails Detalhes do parcelamento, pode ser nulo.
 */
@Serializable
data class Payment(
    val id: Long,
    val amount: Double,
    val type: PaymentType,
    val installmentDetails: InstallmentDetails?
)