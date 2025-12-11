package com.mjtech.store.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mjtech.store.databinding.DialogCartSummaryBinding
import com.mjtech.store.ui.checkout.CheckoutLauncher
import com.mjtech.store.ui.common.currencyFormat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CartSummaryDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogCartSummaryBinding
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCartSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        binding.btnPayCart.setOnClickListener {
            if (activity is CheckoutLauncher) {
                (activity as CheckoutLauncher).launchCheckout()
            } else {
                Log.e(TAG, "Activity must implement CheckoutLauncher")
            }
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartUiState
                    .map { it.totalPrice }
                    .collect { totalPrice ->
                        updateTotalPrice(totalPrice)
                    }
            }
        }
    }

    private fun updateTotalPrice(price: Double) {
        binding.tvCartTotalValue.text = price.currencyFormat()
    }

    companion object {
        const val TAG = "CartSummaryDialog"
    }
}