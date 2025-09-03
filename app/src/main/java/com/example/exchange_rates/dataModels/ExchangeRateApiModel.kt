package com.example.exchange_rates.dataModels
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRatesApiModel(
    val date: String,
    val baseCurrency: String,
    val rates: Map<String, Float>
)

/*
{
          "date": "2024-01-01",
          "base": "USD",
          "rates": {
            "EUR": 0.90,
            "GBP": 0.79,
            "JPY": 148.5
          }
        }
* */