package com.example.exchange_rates.presentation.ui.dashboardCompose

import com.example.exchange_rates.util.TimeSpan

sealed class DashBoardUiEffect {
    data class UpdateHistoricalData(
        val baseCurrency: String,
        val destinationCurrency: String,
        val timeSpan: TimeSpan
    ): DashBoardUiEffect()

    class ClearError: DashBoardUiEffect()
}
