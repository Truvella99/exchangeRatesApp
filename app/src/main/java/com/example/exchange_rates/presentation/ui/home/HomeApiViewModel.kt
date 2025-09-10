package com.example.exchange_rates.presentation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.domain.usecases.FetchLatestExchangeUseCase
import com.example.exchange_rates.domain.usecases.GetCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.exchange_rates.util.Result

// ViewModel tied with Activity Lifecycle scope to avoid calling api more times
@HiltViewModel
class HomeApiViewModel @Inject constructor(
    private val fetchLatestExchangeUseCase: FetchLatestExchangeUseCase,
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _selectedCurrency = MutableLiveData<String>().apply {
        value = "EUR"
    }
    val selectedCurrency: LiveData<String> = _selectedCurrency

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    private val _errorMessage = MutableLiveData<String>().apply {
        value = ""
    }
    val errorMessage: LiveData<String> = _errorMessage

    fun clearError() {
        _errorMessage.value = ""
    }

    private var _currencies = MutableLiveData<List<String>>().apply {
        value = listOf()
    }
    val currencies: LiveData<List<String>> = _currencies

    fun setCurrencies(currencies: List<String>) {
        _currencies.value = currencies
    }

    private var _exchangeRates = MutableLiveData<MutableMap<ExchangeRate, Boolean>>().apply {
        value = mutableMapOf()
    }
    val exchangeRates: LiveData<MutableMap<ExchangeRate, Boolean>> = _exchangeRates

    fun fetchLatestExchangeRates(selectedCurrency: String) {
        // first time empty state, then only if currency changes
        if (_exchangeRates.value.isNullOrEmpty() || _selectedCurrency.value != selectedCurrency) {
            viewModelScope.launch {
                when (val result = fetchLatestExchangeUseCase(selectedCurrency)) {
                    is Result.Success -> _exchangeRates.value = result.data.associateWith { false }.toMutableMap()
                    is Result.Error -> _errorMessage.value = result.message
                }
            }
        }
    }

    fun getAllCurrencies() {
        // only first time when empty state
        if (_currencies.value.isNullOrEmpty()) {
            viewModelScope.launch {
                when (val result = getCurrenciesUseCase()) {
                    is Result.Success -> _currencies.value = result.data
                    is Result.Error -> _errorMessage.value = result.message
                }
            }
        }
    }

    fun toggleFavouriteCurrency(currency: ExchangeRate) {
        val isFavourite = _exchangeRates.value?.get(currency)
        isFavourite?.let { isFavourite ->
            _exchangeRates.value = _exchangeRates.value?.toMutableMap()?.apply {
                put(currency, !isFavourite)
            }
        }
    }
}