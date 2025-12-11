package com.mjtech.store.domain.payment.model

import kotlinx.serialization.Serializable

/**
 * Enumeração que define os tipos de pagamento suportados.
 */
@Serializable
enum class PaymentType(val text: String) {
    DEBIT("Débito"),
    CREDIT("Crédito"),
    PIX("Pix"),
    VOUCHER("Voucher"),
    INSTANT_PAYMENT("Pagamento Instantâneo")
}