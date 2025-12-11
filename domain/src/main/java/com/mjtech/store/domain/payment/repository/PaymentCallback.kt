package com.mjtech.store.domain.payment.repository

interface PaymentCallback {
    fun onSuccess(transactionId: String, message: String? = null)
    fun onFailure(errorCode: String, errorMessage: String)
    fun onCancelled(message: String? = null)
}