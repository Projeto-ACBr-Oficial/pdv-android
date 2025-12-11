package com.mjtech.store.ui.weigh

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mjtech.store.R
import com.mjtech.store.databinding.ActivityWeighBinding
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.weigh.model.PriceSetting
import com.mjtech.store.ui.checkout.CheckoutActivity
import com.mjtech.store.ui.common.components.LoadingDialog
import com.mjtech.store.ui.common.components.SnackbarType
import com.mjtech.store.ui.common.components.showSnackbar
import com.mjtech.store.ui.common.currencyFormat
import com.mjtech.store.ui.common.format
import com.mjtech.store.ui.products.ProductsActivity
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeighActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeighBinding

    private val weighViewModel: WeighViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeighBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initListeners()
        initObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        weighViewModel.deactivate()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weighViewModel.initialLoading.collect { isLoading ->
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
                weighViewModel.uiState
                    .map { it.price }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            is Result.Loading -> {}

                            is Result.Error -> {
                                showSnackbar(
                                    binding.root, getString(R.string.error_price_weight),
                                    SnackbarType.ERROR
                                )
                            }

                            is Result.Success -> {
                                setupPriceDisplay(state.data)
                            }
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weighViewModel.uiState
                    .map { it.initialize }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            is Result.Loading -> {}
                            is Result.Error -> {
                                showSnackbar(binding.root, state.error, SnackbarType.ERROR)
                            }

                            is Result.Success -> {
                                weighViewModel.configScale()
                            }
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weighViewModel.uiState
                    .map { it.configScale }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            is Result.Loading -> {}
                            is Result.Error -> {
                                showSnackbar(binding.root, state.error, SnackbarType.ERROR)
                            }

                            is Result.Success -> {
                                weighViewModel.activate()
                            }
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weighViewModel.uiState
                    .map { it.activate }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            is Result.Loading -> {}
                            is Result.Error -> {
                                showSnackbar(binding.root, state.error, SnackbarType.ERROR)
                            }

                            is Result.Success -> {}
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weighViewModel.uiState
                    .map { it.weight }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            is Result.Loading -> {
                            }

                            is Result.Error -> {
                                showSnackbar(
                                    binding.root, getString(R.string.error_get_weight),
                                    SnackbarType.ERROR
                                )
                            }

                            is Result.Success -> {
                                weighViewModel.calculatePlateValue()
                            }
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weighViewModel.uiState.collect { state ->
                    state.calculationError?.let { error ->
                        showSnackbar(binding.root, error, SnackbarType.ERROR)
                    }
                    if (state.totalValue > 0.0) {
                        setupResultCard(
                            state.totalValue,
                            (state.weight as? Result.Success)?.data ?: 0.0
                        )
                    } else {
                        binding.bottomPanelContainer.visibility = View.INVISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weighViewModel.uiState
                    .map { it.addToCart }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            is Result.Loading -> {
                                LoadingDialog.show(supportFragmentManager)
                            }

                            is Result.Error -> {
                                LoadingDialog.hide(supportFragmentManager)
                                showSnackbar(binding.root, state.error, SnackbarType.ERROR)
                                weighViewModel.resetAddToCartState()
                            }

                            is Result.Success -> {
                                LoadingDialog.hide(supportFragmentManager)
                                showPostAddDialog()
                            }

                            else -> {}
                        }
                    }
            }
        }
    }

    private fun initListeners() {
        binding.fabBack.setOnClickListener {
            finish()
        }

        binding.btnCalculate.setOnClickListener {
            weighViewModel.getWeight()
        }

        binding.btnAddToCart.setOnClickListener {
            weighViewModel.addToCart()
        }
    }

    private fun showPostAddDialog() {
        val dialog = CheckoutWeighDialog()
        dialog.show(supportFragmentManager, "PostAddDialog")
    }

    fun onFinishPlateClick() {
        Intent(this, CheckoutActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    fun onAddMoreClick() {
        Intent(this, ProductsActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun setupPriceDisplay(priceSetting: PriceSetting) {
        binding.tvPrice.text =
            getString(
                R.string.preco_por_,
                priceSetting.unit,
                priceSetting.pricePerKg.currencyFormat()
            )
    }

    private fun setupResultCard(totalValue: Double, weight: Double) {
        binding.bottomPanelContainer.visibility = View.VISIBLE

        binding.tvWeight.text = getString(R.string.peso_kg, weight.format())

        binding.tvFinalValue.text = totalValue.currencyFormat()
    }
}