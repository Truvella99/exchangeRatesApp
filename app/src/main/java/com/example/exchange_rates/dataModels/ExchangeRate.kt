package com.example.exchange_rates.dataModels
import java.time.LocalDate

data class ExchangeRate(
    val baseCurrency: String,
    val destinationCurrency: String,
    val date: LocalDate,
    val exchangeRate: Float
)