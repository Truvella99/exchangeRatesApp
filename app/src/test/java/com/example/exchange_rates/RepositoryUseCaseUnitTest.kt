package com.example.exchange_rates

import com.example.exchange_rates.data.api.ExchangeRatesDataSourceApi
import com.example.exchange_rates.data.repositories.ExchangeRatesRepositoryImpl
import com.example.exchange_rates.domain.usecases.GetCurrenciesUseCase
import com.example.exchange_rates.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RepositoryUseCaseUnitTest {
    private lateinit var exchangeRatesDataSourceApi: ExchangeRatesDataSourceApi
    private lateinit var repository: ExchangeRatesRepositoryImpl
    private lateinit var useCase: GetCurrenciesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        exchangeRatesDataSourceApi = mockk()
        repository = ExchangeRatesRepositoryImpl(exchangeRatesDataSourceApi)
        useCase = GetCurrenciesUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `RepositoryImpl getAllCurrencies returns error when data source returns error`() = runTest {
        // Arrange
        val expectedData = listOf("EUR","CHF","JPY")
        val expectedResult = Result.Success(listOf("EUR","CHF","JPY"))
        coEvery { exchangeRatesDataSourceApi.getAllCurrencies() } returns expectedResult

        // Act
        val result = repository.getAllCurrencies()

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(expectedData, (result as? Result.Success)?.data)
        coVerify { exchangeRatesDataSourceApi.getAllCurrencies() }
    }

    @Test
    fun `UseCase getAllCurrencies returns error when data source returns error`() = runTest {
        // Arrange
        val expectedData = listOf("EUR","CHF","JPY")
        val expectedResult = Result.Success(listOf("EUR","CHF","JPY"))
        coEvery { useCase() } returns expectedResult

        // Act
        val result = repository.getAllCurrencies()

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(expectedData, (result as Result.Success).data)
        coVerify { exchangeRatesDataSourceApi.getAllCurrencies() }
    }
}