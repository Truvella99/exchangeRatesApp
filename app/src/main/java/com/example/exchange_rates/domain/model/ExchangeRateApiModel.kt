package com.example.exchange_rates.domain.model
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi @Serializable
data class ExchangeRatesApiModel(
    @SerialName("base")
    val baseCurrency: String,
    val rates: Map<String, Float>
)