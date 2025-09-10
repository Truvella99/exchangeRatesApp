package com.example.exchange_rates.presentation.ui.homeCompose

import com.example.exchange_rates.domain.model.ExchangeRate

sealed class HomeUiEffect {
    data class FetchNewExchangeRates(val selectedCurrency: String): HomeUiEffect()
    data class UpdateTab(val index: Int): HomeUiEffect()
    data class NavigateToDetail(val baseCurrency: String, val destinationCurrency: String): HomeUiEffect()
    data class UpdateFavourites(val currency: ExchangeRate): HomeUiEffect()
    class ClearError: HomeUiEffect()
}