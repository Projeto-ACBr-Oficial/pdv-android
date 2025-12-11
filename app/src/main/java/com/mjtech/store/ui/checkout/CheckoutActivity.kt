package com.mjtech.store.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mjtech.store.R
import com.mjtech.store.databinding.ActivityCheckoutBinding
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.payment.model.PaymentMethod
import com.mjtech.store.ui.cart.CartViewModel
import com.mjtech.store.ui.common.components.LoadingDialog
import com.mjtech.store.ui.common.components.SnackbarType
import com.mjtech.store.ui.common.components.showSnackbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var paymentAdapter: PaymentMethodAdapter
    private val checkoutViewModel: CheckoutViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initObservers()
        initListeners()
    }

    private fun initViews() {
        binding.appBar.flCartIconContainer.visibility = View.INVISIBLE
        paymentAdapter = PaymentMethodAdapter(checkoutViewModel)
        binding.recyclerViewPaymentMethods.adapter = paymentAdapter
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartUiState
                    .map { it.totalPrice }
                    .distinctUntilChanged()
                    .collect { totalPrice ->
                        checkoutViewModel.setTransactionAmount(totalPrice)
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                checkoutViewModel.uiState
                    .map { it.isLoading }
                    .distinctUntilChanged()
                    .collect { isLoading ->
                        binding.recyclerViewPaymentMethods.isEnabled = !isLoading
                        if (isLoading) {
                            LoadingDialog.show(supportFragmentManager)
                        } else {
                            LoadingDialog.hide(supportFragmentManager)
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                checkoutViewModel.uiState
                    .map { it.availablePaymentMethods }
                    .distinctUntilChanged()
                    .collect { methods ->
                        renderPaymentMethods(methods)
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                checkoutViewModel.uiState
                    .map { state ->
                        val selectedMethod = state.availablePaymentMethods.find {
                            it.id == state.selectedPaymentMethodId
                        }
                        Triple(
                            state.selectedPaymentMethodId,
                            selectedMethod?.requiresInstallments,
                            state.installmentsOptions
                        )
                    }
                    .distinctUntilChanged()
                    .collect { (selectedId, requiresInstallments, installmentResult) ->
                        if (selectedId != null && requiresInstallments == true && installmentResult is Result.Success) {
                            if (supportFragmentManager.findFragmentById(R.id.fragment_container_view) == null) {
                                if (installmentResult.data.isNotEmpty()) {
                                    showInstallmentFragment()
                                }
                            }
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                checkoutViewModel.uiState
                    .map { state ->
                        Pair(state.paymentResult, state.errorMessage)
                    }
                    .distinctUntilChanged()
                    .collect { (paymentResult, errorMessage) ->
                        handlePaymentResult(paymentResult, errorMessage)
                    }
            }
        }
    }

    private fun initListeners() {
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun handlePaymentResult(resultCode: Int?, errorMessage: String? = null) {
        when (resultCode) {
            RESULT_SUCCESS -> {
                val intent = Intent()
                intent.putExtra(CHECKOUT_RESULT_KEY, RESULT_SUCCESS)
                setResult(RESULT_OK, intent)
                finish()
            }

            RESULT_FAILURE -> {
                val intent = Intent()
                intent.putExtra(CHECKOUT_RESULT_KEY, RESULT_FAILURE)
                setResult(RESULT_CANCELED, intent)
                finish()
            }

            RESULT_RETRY -> {
                showSnackbar(
                    anchorView = binding.root,
                    message = errorMessage
                        ?: getString(R.string.error_process_payment_retry),
                    type = SnackbarType.ERROR
                )
                checkoutViewModel.resetPaymentResult()
            }
        }
    }

    private fun renderPaymentMethods(methods: List<PaymentMethod>) {
        paymentAdapter.submitList(methods)
    }

    private fun showInstallmentFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container_view, InstallmentFragment())
            addToBackStack("installment_selection")
        }
    }

    companion object {
        const val CHECKOUT_RESULT_KEY = "checkout_result_key"
        const val RESULT_SUCCESS = 1
        const val RESULT_FAILURE = 2
        const val RESULT_RETRY = 3
    }
}