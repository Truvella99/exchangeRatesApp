package com.example.exchange_rates.repositories

import com.example.exchange_rates.dataModels.ExchangeRate
import com.example.exchange_rates.dataSources.ExchangeRatesDataSource

class ExchangeRatesRepository(
    private val exchangeRatesDataSource: ExchangeRatesDataSource // Network
) {
    suspend fun fetchLatestExchangeRates(): List<ExchangeRate> =
        exchangeRatesDataSource.fetchLatestExchangeRates()
}