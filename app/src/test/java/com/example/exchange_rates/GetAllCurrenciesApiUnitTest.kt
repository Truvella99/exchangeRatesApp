package com.example.exchange_rates

import com.example.exchange_rates.data.api.ExchangeRatesDataSourceApi
import okhttp3.Call
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import com.example.exchange_rates.util.Result

@ExperimentalCoroutinesApi
class GetAllCurrenciesApiUnitTest {

    private lateinit var client: OkHttpClient
    private lateinit var dataSource: ExchangeRatesDataSourceApi

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        client = mockk()
        dataSource = ExchangeRatesDataSourceApi(testDispatcher, client)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllCurrencies returns success when response is valid`() = runTest(testDispatcher) {
        // Arrange
        val expectedJson = """
        {
            "currencies": ["USD", "EUR", "GBP"]
        }
    """.trimIndent()

        val responseBody = expectedJson.toResponseBody("application/json".toMediaType())
        val response = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://api.unirateapi.com/api/currencies").build())
            .body(responseBody)
            .build()

        val call = mockk<Call>()
        every { call.execute() } returns response
        every { client.newCall(any()) } returns call

        // Act
        val result = dataSource.getAllCurrencies()

        // Assert
        assert(result is Result.Success)
        assertEquals(listOf("USD", "EUR", "GBP"), (result as Result.Success).data)
    }

    @Test
    fun `getAllCurrencies returns success but response is not valid`() = runTest(testDispatcher) {
        // Arrange
        val expectedJson = """
        {
            "c": ["USD", "EUR", "GBP"]
        }
    """.trimIndent()

        val responseBody = expectedJson.toResponseBody("application/json".toMediaType())
        val response = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://api.unirateapi.com/api/currencies").build())
            .body(responseBody)
            .build()

        val call = mockk<Call>()
        every { call.execute() } returns response
        every { client.newCall(any()) } returns call

        // Act
        val result = dataSource.getAllCurrencies()

        // Assert
        assert(result is Result.Success)
        assertEquals(listOf(), (result as Result.Success).data)
    }

    @Test
    fun `getAllCurrencies returns error when response is unsuccessful`() = runTest(testDispatcher) {
        // Arrange
        val responseBody = "".toResponseBody("application/json".toMediaType())
        val response = Response.Builder()
            .code(500)
            .message("Internal Server Error")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://api.unirateapi.com/api/currencies").build())
            .body(responseBody)
            .build()

        val call = mockk<Call>()
        every { call.execute() } returns response
        every { client.newCall(any()) } returns call

        // Act
        val result = dataSource.getAllCurrencies()

        // Assert
        assert(result is Result.Error)
    }

    @Test
    fun `getAllCurrencies returns error when response is successful but body is empty`() = runTest(testDispatcher) {
        // Arrange
        val responseBody = "".toResponseBody("application/json".toMediaType())
        val response = Response.Builder()
            .code(200) // Success status
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://api.unirateapi.com/api/currencies").build())
            .body(responseBody) // Empty body
            .build()

        val call = mockk<Call>()
        every { call.execute() } returns response
        every { client.newCall(any()) } returns call

        // Act
        val result = dataSource.getAllCurrencies()

        // Assert
        assert(result is Result.Error)
    }

    @Test
    fun `getAllCurrencies returns error when response is successful but body is null`() = runTest(testDispatcher) {
        // Arrange
        val response = Response.Builder()
            .code(200) // Success status
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://api.unirateapi.com/api/currencies").build())
            .body(null) // Empty body
            .build()

        val call = mockk<Call>()
        every { call.execute() } returns response
        every { client.newCall(any()) } returns call

        // Act
        val result = dataSource.getAllCurrencies()

        // Assert
        assert(result is Result.Error)
    }

}
