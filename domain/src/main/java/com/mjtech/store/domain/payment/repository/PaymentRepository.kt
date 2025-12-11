package com.mjtech.store.domain.payment.repository

import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.payment.model.InstallmentOption
import com.mjtech.store.domain.payment.model.Payment
import com.mjtech.store.domain.payment.model.PaymentMethod
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    fun getAvailablePaymentMethods(): Flow<Result<List<PaymentMethod>>>

    fun getInstallmentOptions(
        methodId: String,
        totalAmount: Double
    ): Flow<Result<List<InstallmentOption>>>

    fun processPayment(payment: Payment): Flow<Result<Unit>>
}