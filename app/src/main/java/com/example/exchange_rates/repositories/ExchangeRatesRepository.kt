package com.example.exchange_rates.repositories

import com.example.exchange_rates.dataModels.ExchangeRate
import com.example.exchange_rates.dataSources.ExchangeRatesDataSource
import java.time.LocalDate
import javax.inject.Inject
import com.example.exchange_rates.ui.util.Result

class ExchangeRatesRepository @Inject constructor(
    private val exchangeRatesDataSource: ExchangeRatesDataSource // Network
) {

    suspend fun getAllCurrencies(): Result<List<String>> =
        exchangeRatesDataSource.getAllCurrencies()

    suspend fun fetchLatestExchangeRates(baseCurrency: String): Result<List<ExchangeRate>> =
        exchangeRatesDataSource.fetchLatestExchangeRates(baseCurrency)

    suspend fun fetchHistoricalTimeSeriesRates(
        baseCurrency: String,
        destinationCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<ExchangeRate>> =
        exchangeRatesDataSource.fetchHistoricalTimeSeriesRates(
            baseCurrency,
            destinationCurrency,
            startDate,
            endDate
        )

}