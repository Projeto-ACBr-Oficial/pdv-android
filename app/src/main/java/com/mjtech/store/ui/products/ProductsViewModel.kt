package com.mjtech.store.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.products.model.Product
import com.mjtech.store.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val productsRepository: ProductsRepository,
) : ViewModel() {

    private val _productsUiState = MutableStateFlow(ProductsUiState())
    val productsUiState: StateFlow<ProductsUiState> = _productsUiState

    private val allProductsFlow: StateFlow<Result<List<Product>>>
        get() = productsRepository.getProducts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = Result.Loading
            )

    init {
        getCategories()
        viewModelScope.launch {
            getFilteredProductsFlow(allProductsFlow, _productsUiState)
                .collectLatest { filteredListResult ->
                    _productsUiState.update { currentState ->
                        currentState.copy(products = filteredListResult)
                    }
                }
        }
    }

    fun onCategorySelected(categoryId: String) {
        _productsUiState.update { currentState ->
            currentState.copy(selectedCategoryId = categoryId)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _productsUiState.update { currentState ->
            currentState.copy(searchQuery = query)
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            productsRepository.getCategories().collect { result ->
                _productsUiState.update { currentState ->
                    currentState.copy(categories = result)
                }
            }
        }
    }

    private fun getFilteredProductsFlow(
        allProductsFlow: Flow<Result<List<Product>>>,
        uiState: StateFlow<ProductsUiState>
    ): StateFlow<Result<List<Product>>> {
        // Filtra os produtos com base nos filtros
        return allProductsFlow.combine(uiState) { productsResult, uiState ->
            when (productsResult) {
                is Result.Success -> {
                    val selectedCategoryId = uiState.selectedCategoryId
                    val searchQuery = uiState.searchQuery

                    var filteredList = productsResult.data

                    if (selectedCategoryId != "0") {
                        filteredList = filteredList.filter { it.categoryId == selectedCategoryId }
                    }
                    if (searchQuery.isNotEmpty()) {
                        filteredList = filteredList.filter { product ->
                            product.name.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    Result.Success(filteredList)
                }

                else -> productsResult
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = Result.Loading
            )
    }
}