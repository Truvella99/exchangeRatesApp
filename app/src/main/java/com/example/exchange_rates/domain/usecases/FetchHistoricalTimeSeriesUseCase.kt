package com.example.exchange_rates.domain.usecases

import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.data.repositories.ExchangeRatesRepositoryImpl
import com.example.exchange_rates.util.Result
import java.time.LocalDate
import javax.inject.Inject

class FetchHistoricalTimeSeriesUseCase @Inject constructor(private val exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl) {
    // using operator function
    suspend operator fun invoke(
        baseCurrency: String,
        destinationCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<ExchangeRate>> {
        return exchangeRatesRepositoryImpl.fetchHistoricalTimeSeriesRates(
            baseCurrency,
            destinationCurrency,
            startDate,
            endDate
        )
    }
}