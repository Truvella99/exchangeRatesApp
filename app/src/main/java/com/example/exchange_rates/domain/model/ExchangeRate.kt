package com.example.exchange_rates.domain.model

import java.time.LocalDate

data class ExchangeRate(
    val baseCurrency: String,
    val destinationCurrency: String,
    val date: LocalDate,
    val exchangeRate: Float
){
    override fun toString(): String {
        return "$destinationCurrency: $exchangeRate"
    }
}