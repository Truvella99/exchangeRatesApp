package com.example.exchange_rates.presentation.ui.homeCompose

import com.example.exchange_rates.domain.model.ExchangeRate

data class HomeUiState(
    val selectedCurrency: String = "EUR",
    val errorMessage: String = "",
    val currencies: List<String> = emptyList(),
    val selectedTabIndex: Int = 0,
    val exchangeRates: Map<ExchangeRate, Boolean> = mapOf<ExchangeRate, Boolean>()
)