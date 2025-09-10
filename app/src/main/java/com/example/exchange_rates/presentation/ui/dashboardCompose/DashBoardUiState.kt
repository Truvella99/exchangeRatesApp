package com.example.exchange_rates.presentation.ui.dashboardCompose

import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.util.TimeSpan

data class DashBoardUiState(
    val selectedTimeSpan: TimeSpan = TimeSpan.HOURS_24,
    val errorMessage: String = "",
    val historicalData: List<ExchangeRate> = listOf()
)
