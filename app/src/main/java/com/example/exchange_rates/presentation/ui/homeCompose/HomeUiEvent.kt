package com.example.exchange_rates.presentation.ui.homeCompose

import com.example.exchange_rates.domain.model.ExchangeRate

sealed class HomeUiEvent {
    data class SelectCurrency(val selectedCurrency: String): HomeUiEvent()
    data class SelectTab(val index: Int): HomeUiEvent()
    data class SelectItem(val baseCurrency: String, val destinationCurrency: String): HomeUiEvent()
    data class ToggleFavouriteCurrency(val currency: ExchangeRate): HomeUiEvent()
    class ClearError(): HomeUiEvent()
}