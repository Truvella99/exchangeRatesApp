package com.example.exchange_rates.data.api

import android.util.Log
import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.data.model.ExchangeRatesApiModel
import com.example.exchange_rates.data.model.HistoricalTimeSeriesApiModel
import com.example.exchange_rates.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import javax.inject.Inject

class ExchangeRatesDataSourceApi @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val client: OkHttpClient
) {
    /**
     * Fetches the latest news from the network and returns the result.
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    private val HOST = "api.unirateapi.com"
    private val API_KEY = "McqqoOq3wPJU6aJXJ5jNxIZQesQbsVzOISaqgROrzIvhKUb6vIIWqAgCQ4xKM8wR"

    private val MOCK_API = false

    suspend fun getAllCurrencies(): Result<List<String>> =
        withContext(ioDispatcher) {
            // Mock In Case Of Down Service
            if(MOCK_API) {
                return@withContext Result.Success(listOf("EUR","CHF","JPY"))
            }

            val urlBuilder = HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegments("api/currencies")
                .addQueryParameter("api_key", API_KEY)

            val url = urlBuilder.build()


            // Create the request
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            // Execute request
            val httpCall = client.newCall(request).execute()
            if (!httpCall.isSuccessful) {
                return@withContext Result.Error("HTTP error ${httpCall.code}: unable to fetch all currencies.")
            }
            val httpBody = httpCall.body?.string()
            if (httpBody.isNullOrEmpty()) {
                return@withContext Result.Error("HTTP error ${httpCall.code}: unable to fetch all currencies.")
            }

            val jsonElement = Json.Default.parseToJsonElement(httpBody)
            val currenciesJsonArray = jsonElement.jsonObject["currencies"]?.jsonArray
            val currenciesList: List<String> =
                currenciesJsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
            Result.Success(currenciesList)
        }

    @OptIn(InternalSerializationApi::class)
    suspend fun fetchLatestExchangeRates(baseCurrency: String): Result<List<ExchangeRate>> =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            // Mock In Case Of Down Service
            if(MOCK_API) {
                return@withContext Result.Success(listOf(
                    ExchangeRate(
                        baseCurrency = "EUR",
                        destinationCurrency = "CHF",
                        date = LocalDate.now(),
                        exchangeRate = 1.24f
                    ),
                    ExchangeRate(
                        baseCurrency = "EUR",
                        destinationCurrency = "JPY",
                        date = LocalDate.now(),
                        exchangeRate = 1.04f
                    )
                ))
            }

            val urlBuilder = HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegments("api/rates")
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("from", baseCurrency)

            val url = urlBuilder.build()


            // Create the request
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            // Execute request
            val httpCall = client.newCall(request).execute()
            if (!httpCall.isSuccessful) {
                return@withContext Result.Error("HTTP error ${httpCall.code}: unable to fetch home latest exchange rates of $baseCurrency")
            }
            val httpBody = httpCall.body?.string()
            if (httpBody.isNullOrEmpty()) {
                return@withContext Result.Error("HTTP error ${httpCall.code}: unable to fetch home latest exchange rates of $baseCurrency")
            }

            val json = Json { ignoreUnknownKeys = true }
            val response = json.decodeFromString<ExchangeRatesApiModel>(httpBody)
            // convert to Exchange Rate
            val date = LocalDate.now()
            val baseCurrency = response.baseCurrency
            val result = response.rates.map { (destinationCurrency, rate) ->
                ExchangeRate(baseCurrency, destinationCurrency, date, rate)
            }
            Result.Success(result)
        }

    @OptIn(InternalSerializationApi::class)
    suspend fun fetchHistoricalTimeSeriesRates(
        baseCurrency: String,
        destinationCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<ExchangeRate>> = withContext(ioDispatcher) {
        // Mock In Case Of Down Service
        if(MOCK_API) {
            return@withContext Result.Success(listOf(
                ExchangeRate(
                    baseCurrency = "EUR",
                    destinationCurrency = "CHF",
                    date = LocalDate.now().minusDays(1),
                    exchangeRate = 1.24f
                ),
                ExchangeRate(
                    baseCurrency = "EUR",
                    destinationCurrency = "CHF",
                    date = LocalDate.now().minusDays(2),
                    exchangeRate = 1.14f
                )
            ))
        }

        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host(HOST)
            .addPathSegments("api/historical/timeseries")
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("start_date", startDate.toString())
            .addQueryParameter("end_date", endDate.toString())
            .addQueryParameter("base", baseCurrency)
            .addQueryParameter("currencies", destinationCurrency)
            .addQueryParameter("format", "json")

        val url = urlBuilder.build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val httpCall = client.newCall(request).execute()
        if (!httpCall.isSuccessful) {
            return@withContext Result.Error("HTTP error ${httpCall.code}: unable to fetch historical data on $destinationCurrency")
        }
        val httpBody = httpCall.body?.string()
        if (httpBody.isNullOrEmpty()) {
            return@withContext Result.Error("HTTP error ${httpCall.code}: unable to fetch historical data on $destinationCurrency")
        }

        val json = Json { ignoreUnknownKeys = true }
        val response = json.decodeFromString<HistoricalTimeSeriesApiModel>(httpBody)

        // Map the response data to List<ExchangeRate>
        val result = response.data.flatMap { (dateString, ratesMap) ->
            val date = LocalDate.parse(dateString)
            ratesMap.map { (destCurrency, rate) ->
                ExchangeRate(
                    baseCurrency = response.base,
                    destinationCurrency = destCurrency,
                    date = date,
                    exchangeRate = rate
                )
            }
        }
        Result.Success(result)
    }
}