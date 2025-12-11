package com.mjtech.store.ui.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.store.domain.cart.repostitory.CartRepository
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.payment.model.InstallmentDetails
import com.mjtech.store.domain.payment.model.InstallmentOption
import com.mjtech.store.domain.payment.model.InstallmentType
import com.mjtech.store.domain.payment.model.Payment
import com.mjtech.store.domain.payment.model.PaymentType
import com.mjtech.store.domain.payment.repository.PaymentCallback
import com.mjtech.store.domain.payment.repository.PaymentProcessor
import com.mjtech.store.domain.payment.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val paymentProcessor: PaymentProcessor,
    private val paymentRepository: PaymentRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val paymentCallback = object : PaymentCallback {
        override fun onSuccess(transactionId: String, message: String?) {
            viewModelScope.launch {
                cartRepository.clearCart()
                    .collect { result ->
                        when (result) {
                            is Result.Success -> {
                                _uiState.update {
                                    it.copy(
                                        paymentResult = CheckoutActivity.RESULT_SUCCESS,
                                        isLoading = false
                                    )
                                }
                            }

                            is Result.Error -> {
                                Log.e(
                                    TAG,
                                    "Failed to clear cart after payment success: ${result.error}"
                                )
                                _uiState.update {
                                    it.copy(
                                        paymentResult = CheckoutActivity.RESULT_SUCCESS,
                                        isLoading = false
                                    )
                                }
                            }

                            is Result.Loading -> {}
                        }
                    }
            }
        }

        override fun onFailure(errorCode: String, errorMessage: String) {
            _uiState.update {
                it.copy(
                    paymentResult = CheckoutActivity.RESULT_RETRY,
                    errorMessage = "$errorCode: $errorMessage",
                    isLoading = false
                )
            }
        }

        override fun onCancelled(message: String?) {
            _uiState.update {
                it.copy(
                    paymentResult = CheckoutActivity.RESULT_RETRY,
                    errorMessage = message,
                    isLoading = false
                )
            }
        }
    }

    init {
        loadPaymentMethods()
    }

    fun setTransactionAmount(amount: Double) {
        _uiState.update { it.copy(transactionAmount = amount) }
    }

    fun onPaymentMethodSelected(methodId: String) {
        val selectedMethod = _uiState.value.availablePaymentMethods.find { it.id == methodId }
        val amount = _uiState.value.transactionAmount

        _uiState.update {
            it.copy(selectedPaymentMethodId = methodId)
        }

        val paymentType = try {
            PaymentType.valueOf(methodId)
        } catch (_: Exception) {
            Log.e(TAG, "Método de pagamento inválido selecionado: $methodId")
            return
        }

        val orderId = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                payment = Payment(
                    id = orderId,
                    amount = amount,
                    type = paymentType,
                    installmentDetails = null
                )
            )
        }

        if (selectedMethod?.requiresInstallments == true && amount > 0.0) {
            loadInstallmentOptions(methodId, amount)
        } else if (selectedMethod?.requiresInstallments == false && amount > 0.0) {
            processPayment()
        }
    }

    fun onInstallmentSelected(installment: InstallmentOption) {
        _uiState.update {
            it.copy(
                payment = it.payment?.copy(
                    installmentDetails = InstallmentDetails(
                        installments = installment.count,
                        installmentType = InstallmentType.MERCHANT
                    )
                )
            )
        }
        processPayment()
    }

    fun processPayment() {
        _uiState.value.apply {
            if (transactionAmount <= 0) {
                _uiState.update {
                    it.copy(
                        paymentResult = CheckoutActivity.RESULT_FAILURE,
                        errorMessage = "Invalid transaction amount"
                    )
                }
                return
            }

            val currentPayment = payment
            if (currentPayment == null) {
                _uiState.update {
                    it.copy(
                        paymentResult = CheckoutActivity.RESULT_FAILURE,
                        errorMessage = "Payment information is missing"
                    )
                }
                return
            }

            paymentProcessor.processPayment(currentPayment, paymentCallback)
        }
    }

    fun resetPaymentResult() {
        _uiState.update { it.copy(paymentResult = null, errorMessage = null) }
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            paymentRepository.getAvailablePaymentMethods()
                .collect { result ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            paymentMethods = result,
                            isLoading = result is Result.Loading
                        )
                    }
                }
        }
    }

    private fun loadInstallmentOptions(methodId: String, amount: Double) {
        viewModelScope.launch {
            paymentRepository.getInstallmentOptions(methodId, amount)
                .collect { result ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            installmentsOptions = result,
                            isLoading = result is Result.Loading
                        )
                    }
                }
        }
    }

    companion object {
        private const val TAG = "CheckoutViewModel"
    }
}