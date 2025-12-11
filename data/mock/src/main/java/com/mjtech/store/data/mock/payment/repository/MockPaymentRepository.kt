package com.mjtech.store.data.mock.payment.repository

import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.payment.model.InstallmentOption
import com.mjtech.store.domain.payment.model.Payment
import com.mjtech.store.domain.payment.model.PaymentMethod
import com.mjtech.store.domain.payment.model.PaymentType
import com.mjtech.store.domain.payment.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class MockPaymentRepository : PaymentRepository {


    override fun getAvailablePaymentMethods(): Flow<Result<List<PaymentMethod>>> = flow {
        emit(Result.Loading)
        val mockMethods = listOf(
            PaymentMethod(
                id = PaymentType.DEBIT.name,
                name = PaymentType.DEBIT.text,
                maxInstallments = 1,
                requiresInstallments = false
            ),
            PaymentMethod(
                id = PaymentType.CREDIT.name,
                name = PaymentType.CREDIT.text,
                maxInstallments = 12,
                requiresInstallments = true
            ),
            PaymentMethod(
                id = PaymentType.PIX.name,
                name = PaymentType.PIX.text,
                maxInstallments = 1,
                requiresInstallments = false
            ),
            PaymentMethod(
                id = PaymentType.VOUCHER.name,
                name = PaymentType.VOUCHER.text,
                maxInstallments = 1,
                requiresInstallments = false
            ),
            PaymentMethod(
                id = PaymentType.INSTANT_PAYMENT.name,
                name = PaymentType.INSTANT_PAYMENT.text,
                maxInstallments = 1,
                requiresInstallments = false
            )
        )
        emit(Result.Success(mockMethods))
    }

    override fun getInstallmentOptions(
        methodId: String,
        totalAmount: Double
    ): Flow<Result<List<InstallmentOption>>> = flow {
        emit(Result.Loading)
        val options = when (methodId) {
            PaymentType.CREDIT.name -> listOf(
                createInstallment(1, totalAmount, 0.0),
                createInstallment(2, totalAmount, 0.0),
                createInstallment(3, totalAmount, 0.0),
                createInstallment(4, totalAmount, 0.0),
                createInstallment(5, totalAmount, 0.0),
                createInstallment(6, totalAmount, 0.0)
            )

            else -> listOf(createInstallment(1, totalAmount, 0.0))
        }
        emit(Result.Success(options))
    }

    override fun processPayment(payment: Payment): Flow<Result<Unit>> = flow {
        emit(Result.Error("Processamento de pagamento não implementado."))
    }

    internal fun createInstallment(count: Int, amount: Double, rate: Double): InstallmentOption {
        val totalWithInterest = amount * (1.0 + rate)
        val monthly = totalWithInterest / count
        return InstallmentOption(
            count = count,
            totalAmount = totalWithInterest,
            interestRate = rate,
            monthlyAmount = monthly
        )
    }
}