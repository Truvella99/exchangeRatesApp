package com.example.exchange_rates.dataSources

import android.util.Log
import com.example.exchange_rates.dataModels.ExchangeRate
import kotlinx.serialization.decodeFromString
import com.example.exchange_rates.dataModels.ExchangeRatesApiModel
import com.example.exchange_rates.dataModels.HistoricalTimeSeriesApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.OkHttpClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExchangeRatesDataSource(
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Fetches the latest news from the network and returns the result.
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    private val client = OkHttpClient()
    private val HOST = "api.unirateapi.com"
    private val API_KEY = "McqqoOq3wPJU6aJXJ5jNxIZQesQbsVzOISaqgROrzIvhKUb6vIIWqAgCQ4xKM8wR"

    suspend fun getAllCurrencies(): List<String> =
        withContext(ioDispatcher) {

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
            val httpResponse = client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("HTTP error ${response.code}: ${response.message}")
                }
                response.body?.string() ?: throw Exception("Empty response body")
            }

            val jsonElement = Json.parseToJsonElement(httpResponse)
            val currenciesJsonArray = jsonElement.jsonObject["currencies"]?.jsonArray

            val currenciesList: List<String> =
                currenciesJsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
            currenciesList
        }

    @OptIn(InternalSerializationApi::class)
    suspend fun fetchLatestExchangeRates(baseCurrency: String): List<ExchangeRate> =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {

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
            val httpResponse = client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("HTTP error ${response.code}: ${response.message} $url")
                }
                response.body?.string() ?: throw Exception("Empty response body")
            }

            val json = Json { ignoreUnknownKeys = true }
            val response = json.decodeFromString<ExchangeRatesApiModel>(httpResponse)
            // convert to Exchange Rate
            val date = LocalDate.now()
            val baseCurrency = response.baseCurrency
            val result = response.rates.map { (destinationCurrency, rate) ->
                ExchangeRate(baseCurrency, destinationCurrency, date, rate)
            }
            result
        }

    @OptIn(InternalSerializationApi::class)
    suspend fun fetchHistoricalTimeSeriesRates(
        baseCurrency: String,
        destinationCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ExchangeRate> = withContext(ioDispatcher) {

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

        val httpResponse = client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP error ${response.code}: ${response.message} $url")
            }
            response.body?.string() ?: throw Exception("Empty response body")
        }

        val json = Json { ignoreUnknownKeys = true }
        val response = json.decodeFromString<HistoricalTimeSeriesApiModel>(httpResponse)

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
        result
    }
}
