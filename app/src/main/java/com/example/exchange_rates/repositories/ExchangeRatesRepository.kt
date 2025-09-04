package com.example.exchange_rates.repositories

import com.example.exchange_rates.dataModels.ExchangeRate
import com.example.exchange_rates.dataSources.ExchangeRatesDataSource
import java.time.LocalDate

class ExchangeRatesRepository(
    private val exchangeRatesDataSource: ExchangeRatesDataSource // Network
) {

    suspend fun getAllCurrencies(): List<String> =
        exchangeRatesDataSource.getAllCurrencies()

    suspend fun fetchLatestExchangeRates(baseCurrency: String): List<ExchangeRate> =
        exchangeRatesDataSource.fetchLatestExchangeRates(baseCurrency)

    suspend fun fetchHistoricalTimeSeriesRates(
        baseCurrency: String,
        destinationCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ExchangeRate> =
        exchangeRatesDataSource.fetchHistoricalTimeSeriesRates(
            baseCurrency,
            destinationCurrency,
            startDate,
            endDate
        )

}