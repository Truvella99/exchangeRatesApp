package com.example.exchange_rates.dataSources

import com.example.exchange_rates.dataModels.ExchangeRate
import kotlinx.serialization.decodeFromString
import com.example.exchange_rates.dataModels.ExchangeRatesApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate

class ExchangeRatesDataSource(
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Fetches the latest news from the network and returns the result.
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    suspend fun fetchLatestExchangeRates(): List<ExchangeRate> =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            val jsonString = """
                {
                  "date": "2024-01-01",
                  "base": "USD",
                  "rates": {
                    "EUR": 0.90,
                    "GBP": 0.79,
                    "JPY": 148.5
                  }
                }
            """.trimIndent()

            val response = Json.decodeFromString<ExchangeRatesApiModel>(jsonString)
            // convert to Exchange Rate
            val date = LocalDate.parse(response.date)
            val baseCurrency = response.baseCurrency
            val result = response.rates.map { (destinationCurrency, rate) ->
                ExchangeRate(baseCurrency, destinationCurrency, date, rate)
            }
            result
        }
}
