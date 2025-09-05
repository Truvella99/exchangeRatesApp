package com.example.exchange_rates.useCases

import com.example.exchange_rates.dataModels.ExchangeRate
import com.example.exchange_rates.repositories.ExchangeRatesRepository
import java.time.LocalDate
import javax.inject.Inject
import com.example.exchange_rates.ui.util.Result

class FetchHistoricalTimeSeriesUseCase @Inject constructor(private val exchangeRatesRepository: ExchangeRatesRepository) {
    // using operator function
    suspend operator fun invoke(
        baseCurrency: String,
        destinationCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<ExchangeRate>> {
        return exchangeRatesRepository.fetchHistoricalTimeSeriesRates(
            baseCurrency,
            destinationCurrency,
            startDate,
            endDate
        )
    }
}