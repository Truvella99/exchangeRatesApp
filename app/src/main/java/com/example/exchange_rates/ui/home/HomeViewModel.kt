package com.example.exchange_rates.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchange_rates.dataModels.ExchangeRate
import com.example.exchange_rates.useCases.FetchHistoricalTimeSeriesUseCase
import com.example.exchange_rates.useCases.FetchLatestExchangeUseCase
import com.example.exchange_rates.useCases.GetCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
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

    private var _currencies = MutableLiveData<List<String>>().apply {
        value = listOf()
    }
    val currencies: LiveData<List<String>> = _currencies

    private var _exchangeRates = MutableLiveData<MutableMap<ExchangeRate, Boolean>>().apply {
        value = mutableMapOf()
    }
    val exchangeRates: LiveData<MutableMap<ExchangeRate, Boolean>> = _exchangeRates

    fun fetchLatestExchangeRates(fetch: Boolean = false) {
        // first time empty state, then only if fetch is passed
        if (_exchangeRates.value!!.isEmpty() || fetch) {
            viewModelScope.launch {
                val latestExchangeRates = fetchLatestExchangeUseCase(_selectedCurrency.value!!)
                _exchangeRates.value = latestExchangeRates.associateWith { false }.toMutableMap()
            }
        }
    }

    fun getAllCurrencies(fetch: Boolean = false) {
        // first time empty state, then only if fetch is passed
        if (_exchangeRates.value!!.isEmpty() || fetch) {
            viewModelScope.launch {
                val currencies = getCurrenciesUseCase()
                _currencies.value = currencies
            }
        }
    }

    fun toggleFavouriteCurrency(currency: ExchangeRate) {
        val isFavourite = _exchangeRates.value?.get(currency)!!
        _exchangeRates.value = _exchangeRates.value?.toMutableMap()?.apply {
            put(currency, !isFavourite)
        }
//        Log.e("HomeFragment", _exchangeRates.value?.filter { it.value }.toString())
    }

    // 0 for Favorites, 1 for Others
    private val _selectedTabIndex = MutableLiveData(0)
    val selectedTabIndex: LiveData<Int> get() = _selectedTabIndex

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
    }

}