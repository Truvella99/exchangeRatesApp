package com.example.exchange_rates.data.repositories

import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.data.api.ExchangeRatesDataSourceApi
import com.example.exchange_rates.domain.repositories.ExchangeRatesRepository
import com.example.exchange_rates.util.Result
import java.time.LocalDate
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val exchangeRatesDataSourceApi: ExchangeRatesDataSourceApi // Network
): ExchangeRatesRepository {

    override suspend fun getAllCurrencies(): Result<List<String>> =
        exchangeRatesDataSourceApi.getAllCurrencies()

    override suspend fun fetchLatestExchangeRates(baseCurrency: String): Result<List<ExchangeRate>> =
        exchangeRatesDataSourceApi.fetchLatestExchangeRates(baseCurrency)

    override suspend fun fetchHistoricalTimeSeriesRates(
        baseCurrency: String,
        destinationCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<ExchangeRate>> =
        exchangeRatesDataSourceApi.fetchHistoricalTimeSeriesRates(
            baseCurrency,
            destinationCurrency,
            startDate,
            endDate
        )

}