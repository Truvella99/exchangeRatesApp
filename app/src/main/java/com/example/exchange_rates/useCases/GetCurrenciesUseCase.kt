package com.example.exchange_rates.useCases

import com.example.exchange_rates.repositories.ExchangeRatesRepository
import javax.inject.Inject
import com.example.exchange_rates.ui.util.Result

class GetCurrenciesUseCase @Inject constructor(private val exchangeRatesRepository: ExchangeRatesRepository) {
    // using operator function
    suspend operator fun invoke(): Result<List<String>> {
        return exchangeRatesRepository.getAllCurrencies()
    }
}