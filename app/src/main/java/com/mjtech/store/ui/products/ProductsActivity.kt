package com.mjtech.store.ui.products

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.mjtech.store.R
import com.mjtech.store.databinding.ActivityProductsBinding
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.products.model.Category
import com.mjtech.store.ui.cart.CartSummaryDialog
import com.mjtech.store.ui.cart.CartViewModel
import com.mjtech.store.ui.checkout.CheckoutActivity
import com.mjtech.store.ui.checkout.CheckoutLauncher
import com.mjtech.store.ui.common.components.AppBarDrawer
import com.mjtech.store.ui.common.components.LoadingDialog
import com.mjtech.store.ui.common.components.SnackbarType
import com.mjtech.store.ui.common.components.showSnackbar
import com.mjtech.store.ui.common.getAppVersionName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalBadgeUtils::class)
class ProductsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    CheckoutLauncher {

    private lateinit var binding: ActivityProductsBinding
    private lateinit var productsAdapter: ProductsAdapter
    private val productsViewModel: ProductsViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()
    private var cartBadge: BadgeDrawable? = null
    private lateinit var drawerLayout: DrawerLayout
    private var lastCheckedChipId: Int = 0

    private val checkoutResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null) {
            val checkoutStatus = result.data?.getIntExtra(CheckoutActivity.CHECKOUT_RESULT_KEY, 0)

            when (checkoutStatus) {
                CheckoutActivity.RESULT_SUCCESS -> {
                    showSnackbar(
                        anchorView = binding.mainLayout,
                        message = getString(R.string.success_message_buy),
                        type = SnackbarType.SUCCESS
                    )
                }

                CheckoutActivity.RESULT_FAILURE -> {
                    showSnackbar(
                        anchorView = binding.mainLayout,
                        message = getString(R.string.error_process_payment),
                        type = SnackbarType.ERROR
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        initObservers()
    }

    private fun initComponents() {
        setupDrawer()
        setupBadgeCart()
        setupSearch()
        setupProductsList()
        setupFabButton()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productsViewModel.productsUiState
                    .map { it.categories }
                    .distinctUntilChanged()
                    .collect { categoriesResult ->
                        when (categoriesResult) {
                            is Result.Success -> {
                                createCategoriesChips(categoriesResult.data)
                                setCategorySelectionListener()
                            }

                            is Result.Error -> {
                                showSnackbar(
                                    binding.mainLayout,
                                    getString(R.string.error_loading_categories),
                                    SnackbarType.ERROR
                                )
                                Log.e(
                                    TAG,
                                    categoriesResult.error
                                )
                            }

                            is Result.Loading -> {}
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartUiState
                    .map { it.addItemState }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            is Result.Error -> {
                                showSnackbar(
                                    binding.mainLayout,
                                    getString(R.string.error_add_cart_item),
                                    SnackbarType.ERROR
                                )
                                Log.e(
                                    TAG,
                                    state.error
                                )
                                cartViewModel.resetAddItemState()
                            }

                            is Result.Loading -> {}
                            is Result.Success -> {}
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartUiState
                    .map { it.removeItemState }
                    .distinctUntilChanged()
                    .collect { state ->
                        when (state) {
                            is Result.Error -> {
                                showSnackbar(
                                    binding.mainLayout,
                                    getString(R.string.error_remove_cart_item),
                                    SnackbarType.ERROR
                                )
                                Log.e(
                                    TAG,
                                    state.error
                                )
                                cartViewModel.resetRemoveItemState()
                            }

                            is Result.Loading -> {}
                            is Result.Success -> {}
                        }
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartUiState
                    .map { it.quantitiesProducts }
                    .distinctUntilChanged()
                    .collect { quantitiesMap ->
                        productsAdapter.updateQuantities(quantitiesMap)
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartUiState
                    .map { it.totalItemsCount }
                    .distinctUntilChanged()
                    .collect { count ->
                        updateCartBadge(count)
                        showFabButton(count)
                    }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productsViewModel.productsUiState.collect { uiState ->
                    // Verifica se algum dado está carregando
                    val isAnythingLoading =
                        uiState.categories is Result.Loading ||
                                uiState.products is Result.Loading

                    if (isAnythingLoading) {
                        LoadingDialog.show(supportFragmentManager)
                    } else {
                        lifecycleScope.launch {
                            delay(50)
                            LoadingDialog.hide(supportFragmentManager)
                        }
                    }

                    when (uiState.products) {
                        is Result.Success -> {
                            checkProductsEmpty(uiState.products.data.size)
                            productsAdapter.submitList(uiState.products.data)
                        }

                        is Result.Error -> {
                            showSnackbar(
                                binding.mainLayout,
                                getString(R.string.error_loading_products),
                                SnackbarType.ERROR
                            )
                            Log.e(
                                TAG,
                                uiState.products.error
                            )
                        }

                        is Result.Loading -> {}
                    }
                }
            }
        }
    }

    // Navigation Drawer

    private fun setupDrawer() {
        drawerLayout = binding.drawerLayout

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBar.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val drawable = toggle.drawerArrowDrawable
        val tintColor = ContextCompat.getColor(this, R.color.white)
        @Suppress("DEPRECATION")
        drawable.setColorFilter(tintColor, android.graphics.PorterDuff.Mode.SRC_IN)

        binding.appBarDrawer.setNavigationListener(this)

        binding.appBarDrawer.setItemActive(AppBarDrawer.ItemDrawer.HOME)

        binding.appBarDrawer.setVersionName(getAppVersionName(packageManager, packageName))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {}
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Cart

    private fun setupBadgeCart() {
        binding.appBar.flCartIconContainer.setOnClickListener {
            val currentCount = cartViewModel.cartUiState.value.totalItemsCount
            if (currentCount > 0) {
                showCartDialog()
            } else {
                showSnackbar(
                    binding.mainLayout,
                    getString(R.string.empty_cart),
                    SnackbarType.INFO
                )
            }
        }
    }

    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            if (cartBadge == null) {
                cartBadge = BadgeDrawable.create(this@ProductsActivity).apply {
                    isVisible = true
                    backgroundColor = ContextCompat.getColor(this@ProductsActivity, R.color.red)
                    badgeTextColor = ContextCompat.getColor(this@ProductsActivity, R.color.white)
                    maxCharacterCount = 3
                }
                binding.appBar.flCartIconContainer.post {
                    cartBadge?.let { badge ->
                        BadgeUtils.attachBadgeDrawable(
                            badge,
                            binding.appBar.ivCartIcon,
                            binding.appBar.flCartIconContainer
                        )
                    }
                }
            }
            cartBadge?.number = count
            cartBadge?.isVisible = true

        } else {
            cartBadge?.isVisible = false
            binding.appBar.flCartIconContainer.post {
                cartBadge?.let { badge ->
                    BadgeUtils.detachBadgeDrawable(
                        badge,
                        binding.appBar.ivCartIcon
                    )
                }
                cartBadge = null
            }
        }
    }

    private fun showCartDialog() {
        val existingBottomSheet =
            supportFragmentManager.findFragmentByTag(CartSummaryDialog.Companion.TAG) as? CartSummaryDialog

        if (existingBottomSheet == null || !existingBottomSheet.isAdded) {
            val newBottomSheet = CartSummaryDialog()
            newBottomSheet.show(supportFragmentManager, CartSummaryDialog.Companion.TAG)
        }
    }

    // Search

    private fun setupSearch() {
        binding.searchTextInput.setEndIconOnClickListener {
            binding.searchEditText.setText("")
            productsViewModel.onSearchQueryChanged("")
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                productsViewModel.onSearchQueryChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Categories Chips

    private fun createCategoriesChips(categories: List<Category>) {
        val categories = listOf(Category(id = "0", name = getString(R.string.todos))) + categories
        binding.chipGroupCategories.removeAllViews()

        var firstChipId = -1

        categories.forEachIndexed { index, category ->
            val chip = getCategoryChip(category)
            binding.chipGroupCategories.addView(chip)
            if (index == 0) {
                firstChipId = chip.id
            }
        }

        if (firstChipId != -1) {
            binding.chipGroupCategories.check(firstChipId)
            lastCheckedChipId = firstChipId
        }
    }

    private fun getCategoryChip(category: Category): Chip {
        val chip = Chip(this)

        val chipStyle = ChipDrawable.createFromAttributes(this, null, 0, R.style.StoreChip)
        chip.setChipDrawable(chipStyle)

        chip.setTextAppearanceResource(R.style.StoreChip)

        chip.id = View.generateViewId()
        chip.tag = category.id
        chip.text = category.name
        chip.isCheckable = true
        chip.isClickable = true

        val layoutParams = ChipGroup.LayoutParams(
            ChipGroup.LayoutParams.WRAP_CONTENT,
            ChipGroup.LayoutParams.WRAP_CONTENT
        )
        chip.layoutParams = layoutParams

        return chip
    }

    private fun setCategorySelectionListener() {
        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                group.post {
                    group.check(lastCheckedChipId)
                }
                return@setOnCheckedStateChangeListener
            }
            val currentCheckedId = checkedIds[0]
            lastCheckedChipId = currentCheckedId
            val selectedCategoryTagChip = group.findViewById<Chip>(currentCheckedId).tag.toString()

            productsViewModel.onCategorySelected(selectedCategoryTagChip)
        }
    }

    // Products RecyclerView

    private fun setupProductsList() {
        productsAdapter = ProductsAdapter(
            onAddItemClicked = { product ->
                cartViewModel.onAddProductToCart(product)
            },
            onRemoveItemClicked = { product ->
                cartViewModel.onRemoveProductFromCart(product)
            }
        )

        binding.recyclerViewProducts.layoutManager = GridLayoutManager(this, calculateNoOfColumns())
        binding.recyclerViewProducts.adapter = productsAdapter
    }

    private fun calculateNoOfColumns(): Int {
        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val noOfColumns = (dpWidth / 180).toInt()
        return if (noOfColumns < 2) 2 else noOfColumns
    }

    private fun checkProductsEmpty(totalProducts: Int) {
        if (totalProducts > 0) {
            binding.tvNoProductsFound.visibility = View.GONE
            binding.recyclerViewProducts.visibility = View.VISIBLE
        } else {
            binding.tvNoProductsFound.visibility = View.VISIBLE
            binding.recyclerViewProducts.visibility = View.GONE
        }

    }

    // FAB

    private fun setupFabButton() {
        binding.fabCart.setOnClickListener {
            showCartDialog()
        }

        binding.fabBack.setOnClickListener {
            finish()
        }
    }

    private fun showFabButton(cartProducts: Int) {
        if (cartProducts > 0) {
            if (!binding.fabCart.isShown) {
                binding.fabCart.show()
            }
        } else {
            binding.fabCart.hide()
        }
    }

    // Implementations

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    binding.root.requestFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun launchCheckout() {
        val dialog =
            supportFragmentManager.findFragmentByTag(CartSummaryDialog.TAG) as? CartSummaryDialog
        dialog?.dismiss()

        val checkoutIntent = Intent(this, CheckoutActivity::class.java)
        checkoutResultLauncher.launch(checkoutIntent)
    }

    companion object {
        const val TAG = "ProductsActivity"
    }
}