package com.mjtech.store.domain.payment.model

import kotlinx.serialization.Serializable

/**
 * Enumeração que define os tipos de parcelamentos suportados.
 *
 * [NONE] Parcelamento à vista.
 * [MERCHANT] Parcelamento sem juros.
 * [ISSUER] Parcelamento com juros.
 */
@Serializable
enum class InstallmentType(val text: String) {
    NONE("NONE"), MERCHANT("MERCHANT"), ISSUER("ISSUER")
}