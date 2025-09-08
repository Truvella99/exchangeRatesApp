package com.example.exchange_rates.domain.usecases

import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.data.repositories.ExchangeRatesRepositoryImpl
import com.example.exchange_rates.util.Result
import javax.inject.Inject

class FetchLatestExchangeUseCase @Inject constructor(private val exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl) {
    // using operator function
    suspend operator fun invoke(baseCurrency: String): Result<List<ExchangeRate>> {
        return exchangeRatesRepositoryImpl.fetchLatestExchangeRates(baseCurrency)
    }
}