package com.example.exchange_rates.presentation.ui.homeCompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.domain.usecases.FetchLatestExchangeUseCase
import com.example.exchange_rates.domain.usecases.GetCurrenciesUseCase
import com.example.exchange_rates.presentation.ui.homeCompose.HomeUiEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.exchange_rates.util.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ViewModel tied with Activity Lifecycle scope to avoid calling api more times
@HiltViewModel
class HomeViewModelCompose @Inject constructor(
    private val fetchLatestExchangeUseCase: FetchLatestExchangeUseCase,
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<HomeUiEffect>()
    val uiEffectFlow = _uiEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            val currencies = getAllCurrencies()
            currencies?.let { result ->
                when(result) {
                    is Result.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                    is Result.Success<List<String>> -> _uiState.update { it.copy(currencies = result.data) }
                }
            }
            val exchangeRates = fetchLatestExchangeRates(_uiState.value.selectedCurrency)
            exchangeRates?.let { result ->
                when(result) {
                    is Result.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                    is Result.Success<List<ExchangeRate>> -> _uiState.update { it.copy(exchangeRates = result.data.associateWith { false }.toMutableMap()) }
                }
            }
        }
    }
    fun onEvent(event: HomeUiEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeUiEvent.SelectItem ->
                    _uiEffect.emit(
                        NavigateToDetail(
                            event.baseCurrency,
                            event.destinationCurrency)
                    )
                is HomeUiEvent.SelectCurrency ->
                    _uiEffect.emit(
                        FetchNewExchangeRates(
                            event.selectedCurrency)
                    )
                is HomeUiEvent.SelectTab ->
                    _uiEffect.emit(UpdateTab(event.index))
                is HomeUiEvent.ToggleFavouriteCurrency ->
                    _uiEffect.emit(UpdateFavourites(event.currency))

                is HomeUiEvent.ClearError ->
                    _uiEffect.emit(ClearError())
            }
        }
    }

    suspend fun fetchNewExchangeRates(selectedCurrency: String) {
        val exchangeRates = fetchLatestExchangeRates(selectedCurrency)
        _uiState.update { it.copy(selectedCurrency = selectedCurrency) }
        exchangeRates?.let { result ->
            when(result) {
                is Result.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                is Result.Success<List<ExchangeRate>> -> _uiState.update { it.copy(exchangeRates = result.data.associateWith { false }.toMutableMap()) }
            }
        }
    }

    fun updateTab(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun toggleFavouriteCurrency(currency: ExchangeRate) {
        _uiState.update {
            val isFavourite = it.exchangeRates[currency]
            if (isFavourite == null) {
                it
            } else {
                val newExchangeRates = it.exchangeRates.toMutableMap().apply {
                    put(currency, !isFavourite)
                }
                it.copy(exchangeRates = newExchangeRates)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }

    private suspend fun getAllCurrencies(): Result<List<String>>? {
        // only first time when empty state
        if (_uiState.value.currencies.isEmpty()) {
            return getCurrenciesUseCase()
        }
        return null
    }

    private suspend fun fetchLatestExchangeRates(selectedCurrency: String): Result<List<ExchangeRate>>? {
        // first time empty state, then only if currency changes
        if (_uiState.value.exchangeRates.isEmpty() || _uiState.value.selectedCurrency != selectedCurrency) {
            return fetchLatestExchangeUseCase(selectedCurrency)
        }
        return null
    }

}