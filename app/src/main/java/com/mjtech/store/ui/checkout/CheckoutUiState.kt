package com.mjtech.store.ui.checkout

import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.payment.model.InstallmentOption
import com.mjtech.store.domain.payment.model.Payment
import com.mjtech.store.domain.payment.model.PaymentMethod

data class CheckoutUiState(
    val transactionAmount: Double = 0.0,
    val payment: Payment? = null,
    val paymentResult: Int? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val selectedPaymentMethodId: String? = null,
    val paymentMethods: Result<List<PaymentMethod>>? = null,
    val installmentsOptions: Result<List<InstallmentOption>>? = null
) {
    val availablePaymentMethods: List<PaymentMethod>
        get() = (paymentMethods as? Result.Success)?.data ?: emptyList()
}