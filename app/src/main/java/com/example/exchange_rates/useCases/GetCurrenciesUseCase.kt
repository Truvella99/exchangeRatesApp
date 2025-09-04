package com.example.exchange_rates.useCases

import com.example.exchange_rates.repositories.ExchangeRatesRepository
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(private val exchangeRatesRepository: ExchangeRatesRepository) {
    // using operator function
    suspend operator fun invoke(): List<String> {
        return exchangeRatesRepository.getAllCurrencies()
    }
}