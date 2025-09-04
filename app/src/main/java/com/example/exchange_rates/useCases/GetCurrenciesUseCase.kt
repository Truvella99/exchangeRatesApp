package com.example.exchange_rates.useCases

import com.example.exchange_rates.repositories.ExchangeRatesRepository

class GetCurrenciesUseCase(private val exchangeRatesRepository: ExchangeRatesRepository) {
    // using operator function
    suspend operator fun invoke(): List<String> {
        return exchangeRatesRepository.getAllCurrencies()
    }
}