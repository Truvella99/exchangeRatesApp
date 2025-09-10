package com.example.exchange_rates.presentation.ui.dashboardCompose

import com.example.exchange_rates.util.TimeSpan

sealed class DashBoardUiEvent {
    data class SelectTimeSpan(
        val baseCurrency: String,
        val destinationCurrency: String,
        val timeSpan: TimeSpan
    ): DashBoardUiEvent()

    class ClearError(): DashBoardUiEvent()
}
