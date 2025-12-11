package com.mjtech.store.domain.payment.repository

import com.mjtech.store.domain.payment.model.Payment

interface PaymentProcessor {

    fun processPayment(payment: Payment, callback: PaymentCallback)
}