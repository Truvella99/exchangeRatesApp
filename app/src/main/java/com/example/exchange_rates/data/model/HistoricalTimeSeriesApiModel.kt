package com.example.exchange_rates.data.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class HistoricalTimeSeriesApiModel(
    @SerialName("start_date")
    val startDate: String,
    @SerialName("end_date")
    val endDate: String,
    val base: String,
    val data: Map<String, Map<String, Float>>
)