package com.example.exchange_rates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.exchange_rates.domain.usecases.FetchLatestExchangeUseCase
import com.example.exchange_rates.domain.usecases.GetCurrenciesUseCase
import com.example.exchange_rates.presentation.ui.home.HomeApiViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import com.example.exchange_rates.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class HomeApiViewModelUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fetchLatestExchangeUseCase: FetchLatestExchangeUseCase
    private lateinit var getCurrenciesUseCase: GetCurrenciesUseCase
    private lateinit var homeApiViewModel :HomeApiViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        fetchLatestExchangeUseCase = mockk()
        getCurrenciesUseCase = mockk()
        homeApiViewModel = HomeApiViewModel(fetchLatestExchangeUseCase,getCurrenciesUseCase)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllCurrencies when state is empty and use case returns success should update currencies`() = runTest(testDispatcher) {
        // Arrange
        val expectedCurrencies = listOf("USD", "EUR", "GBP")
        val successResult = Result.Success(expectedCurrencies)
        coEvery { getCurrenciesUseCase() } returns successResult

        // Verify initial state is empty
        assertTrue(homeApiViewModel.currencies.value.isNullOrEmpty())

        // Act
        homeApiViewModel.getAllCurrencies()
        advanceUntilIdle() // Wait for coroutine to complete

        // Assert
        assertEquals(expectedCurrencies, homeApiViewModel.currencies.value)
        coVerify(exactly = 1) { getCurrenciesUseCase() }
    }

    @Test
    fun `getAllCurrencies when state is empty and use case returns error should update error message`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "Network error"
        val errorResult = Result.Error(errorMessage)
        coEvery { getCurrenciesUseCase() } returns errorResult

        // Verify initial state is empty
        assertTrue(homeApiViewModel.currencies.value.isNullOrEmpty())

        // Act
        homeApiViewModel.getAllCurrencies()
        advanceUntilIdle() // Wait for coroutine to complete

        // Assert
        assertEquals(errorMessage, homeApiViewModel.errorMessage.value)
        assertTrue(homeApiViewModel.currencies.value.isNullOrEmpty()) // Currencies should remain empty
        coVerify(exactly = 1) { getCurrenciesUseCase() }
    }

    @Test
    fun `getAllCurrencies when state is not empty should not call use case`() = runTest(testDispatcher) {
        // Arrange - Pre-populate currencies to make state non-empty
        val existingCurrencies = listOf("USD", "EUR")
        // You'll need to set the initial state somehow. This depends on your ViewModel implementation.
        // If you have a method to set currencies or if the constructor can take initial state:
        homeApiViewModel.setCurrencies(existingCurrencies)

        // Act
        homeApiViewModel.getAllCurrencies()
        advanceUntilIdle()

        // Assert
        assertEquals(existingCurrencies, homeApiViewModel.currencies.value)
        coVerify(exactly = 0) { getCurrenciesUseCase() } // Should not be called
    }

}