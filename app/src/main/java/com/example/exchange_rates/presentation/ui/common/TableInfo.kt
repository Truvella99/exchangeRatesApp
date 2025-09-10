package com.example.exchange_rates.presentation.ui.common
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.exchange_rates.domain.model.ExchangeRate

data class TableInfo(
    val baseCurrencyColumnTitle : String,
    val destinationCurrencyColumnTitle : String,
    val exchangeRateColumnTitle : String,
    val icon: ImageVector,
    val baseCurrencyColumnWeight: Float,
    val destinationCurrencyColumnWeight: Float,
    val exchangeRateColumnWeight: Float,
    val column4Weight: Float,
    val data: List<ExchangeRate>,
    val isDataFavourite: List<Boolean>? = null
)
