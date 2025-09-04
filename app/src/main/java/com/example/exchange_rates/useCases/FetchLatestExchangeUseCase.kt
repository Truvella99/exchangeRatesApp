package com.example.exchange_rates.useCases

import com.example.exchange_rates.dataModels.ExchangeRate
import com.example.exchange_rates.repositories.ExchangeRatesRepository
import javax.inject.Inject

class FetchLatestExchangeUseCase @Inject constructor(private val exchangeRatesRepository: ExchangeRatesRepository) {
    // using operator function
    suspend operator fun invoke(baseCurrency: String): List<ExchangeRate> {
        return exchangeRatesRepository.fetchLatestExchangeRates(baseCurrency)
    }
}