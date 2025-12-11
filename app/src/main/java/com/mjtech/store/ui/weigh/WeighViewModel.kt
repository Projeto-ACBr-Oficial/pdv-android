package com.mjtech.store.ui.weigh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjtech.store.domain.cart.repostitory.CartRepository
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.products.model.Product
import com.mjtech.store.domain.weigh.repository.PricingRepository
import com.mjtech.store.domain.weigh.repository.ScaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeighViewModel(
    private val pricingRepository: PricingRepository,
    private val scaleRepository: ScaleRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeighUiState())
    val uiState: StateFlow<WeighUiState> = _uiState

    val initialLoading: StateFlow<Boolean> = _uiState
        .map { state ->
            val priceDone = state.price !is Result.Loading
            val configDone = state.configScale !is Result.Loading
            val initializedDone = state.initialize !is Result.Loading
            val activatedDone = state.activate !is Result.Loading

            val initialTasksDone = priceDone && configDone && initializedDone && activatedDone

            !initialTasksDone
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    init {
        initialize()
        getPriceSetting()
    }

    fun getWeight() {
        viewModelScope.launch {
            scaleRepository.lerPeso().collect { weightResult ->
                _uiState.update { currentState ->
                    currentState.copy(weight = weightResult)
                }
            }
        }
    }

    fun configScale() {
        viewModelScope.launch {
            scaleRepository.configurarBalanca().collect { configurarResult ->
                _uiState.update { currentState ->
                    currentState.copy(configScale = configurarResult)
                }
            }
        }
    }

    fun activate() {
        viewModelScope.launch {
            scaleRepository.ativar().collect { ativarResult ->
                _uiState.update { currentState ->
                    currentState.copy(activate = ativarResult)
                }
            }
        }
    }

    fun deactivate() {
        viewModelScope.launch {
            scaleRepository.desativar().collect { desativarResult ->
                _uiState.update { currentState ->
                    currentState.copy(deactivate = desativarResult)
                }

                if (desativarResult is Result.Success) {
                    finishScaleResource()
                }
            }
        }
    }

    fun calculatePlateValue() {
        val currentState = _uiState.value

        _uiState.update { it.copy(calculationError = null) }

        val weightResult = currentState.weight
        val priceResult = currentState.price

        val calculatedValue = try {
            val weight = (weightResult as? Result.Success)?.data
            val price = (priceResult as? Result.Success)?.data?.pricePerKg

            if (weight == null || price == null) {
                throw IllegalStateException("Peso ou Preço não estão disponíveis ou estáveis.")
            }
            weight * price

        } catch (e: Exception) {
            _uiState.update { it.copy(calculationError = "Erro ao calcular: ${e.message}") }
            return
        }
        _uiState.update {
            it.copy(totalValue = calculatedValue)
        }
    }

    fun addToCart() {
        val currentState = _uiState.value

        val weightSuccess = (currentState.weight as? Result.Success)?.data

        val total = currentState.totalValue

        if (total <= 0.0 || weightSuccess == null) {
            _uiState.update {
                it.copy(
                    addToCart = Result.Error("O valor ou peso não são válidos para adicionar."),
                )
            }
            return
        }

        val product = Product(
            id = "000",
            categoryId = "0",
            name = "Prato por KG",
            description = "",
            price = total,
            imageUrl = "",
            isAvailable = true
        )

        viewModelScope.launch {
            cartRepository.addItem(product).collect { result ->
                _uiState.update { currentState ->
                    currentState.copy(addToCart = result)
                }
            }
        }
    }


    fun resetAddToCartState() {
        _uiState.update { it.copy(addToCart = null) }
    }

    private fun getPriceSetting() {
        viewModelScope.launch {
            pricingRepository.getPriceSetting().collect { priceResult ->
                _uiState.update { currentState ->
                    currentState.copy(price = priceResult)
                }
            }
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            scaleRepository.inicializar().collect { inicializarResult ->
                _uiState.update { currentState ->
                    currentState.copy(initialize = inicializarResult)
                }
            }
        }
    }

    private fun finishScaleResource() {
        viewModelScope.launch {
            scaleRepository.finalizar().collect { finalizarResult ->
                _uiState.update { currentState ->
                    currentState.copy(finish = finalizarResult)
                }
            }
        }
    }
}