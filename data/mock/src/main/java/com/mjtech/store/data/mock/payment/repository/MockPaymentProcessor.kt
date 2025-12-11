package com.mjtech.store.data.mock.payment.repository

import com.mjtech.store.domain.payment.model.Payment
import com.mjtech.store.domain.payment.repository.PaymentCallback
import com.mjtech.store.domain.payment.repository.PaymentProcessor

internal class MockPaymentProcessor : PaymentProcessor {

    override fun processPayment(
        payment: Payment,
        callback: PaymentCallback
    ) {
        if (payment.amount <= 100) {
            callback.onSuccess(
                "ID",
                "Simulated payment successful for amount: ${payment.amount}"
            )
        } else {
            callback.onFailure(
                "001",
                "Simulated payment failure for amount: ${payment.amount}"
            )
        }
    }
}