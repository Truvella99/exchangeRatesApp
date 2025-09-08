package com.example.exchange_rates.domain.repositories
import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.util.Result
import java.time.LocalDate

interface ExchangeRatesRepository {

    suspend fun getAllCurrencies(): Result<List<String>>

    suspend fun fetchLatestExchangeRates(baseCurrency: String): Result<List<ExchangeRate>>

    suspend fun fetchHistoricalTimeSeriesRates(
        baseCurrency: String,
        destinationCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<ExchangeRate>>
}
