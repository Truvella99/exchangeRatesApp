package com.example.exchange_rates.domain.usecases

import com.example.exchange_rates.data.repositories.ExchangeRatesRepositoryImpl
import com.example.exchange_rates.util.Result
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(private val exchangeRatesRepositoryImpl: ExchangeRatesRepositoryImpl) {
    // using operator function
    suspend operator fun invoke(): Result<List<String>> {
        return exchangeRatesRepositoryImpl.getAllCurrencies()
    }
}