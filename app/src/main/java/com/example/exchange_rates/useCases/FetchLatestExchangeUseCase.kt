package com.example.exchange_rates.useCases

import com.example.exchange_rates.dataModels.ExchangeRate
import com.example.exchange_rates.repositories.ExchangeRatesRepository
import javax.inject.Inject
import com.example.exchange_rates.ui.util.Result

class FetchLatestExchangeUseCase @Inject constructor(private val exchangeRatesRepository: ExchangeRatesRepository) {
    // using operator function
    suspend operator fun invoke(baseCurrency: String): Result<List<ExchangeRate>> {
        return exchangeRatesRepository.fetchLatestExchangeRates(baseCurrency)
    }
}