package com.example.exchange_rates.presentation.ui.dashboardCompose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.util.Result
import com.example.exchange_rates.util.TimeSpan
import com.example.exchange_rates.domain.usecases.FetchHistoricalTimeSeriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fetchHistoricalTimeSeriesUseCase: FetchHistoricalTimeSeriesUseCase
) : ViewModel() {

    private val _selectedTimeSpan = MutableLiveData<TimeSpan>().apply {
        value = TimeSpan.HOURS_24
    }
    val selectedTimeSpan: LiveData<TimeSpan> = _selectedTimeSpan

    fun setTimeSpan(timeSpan: String) {
        _selectedTimeSpan.value = TimeSpan.fromDisplayName(timeSpan)
    }

    private val _errorMessage = MutableLiveData<String>().apply {
        value = ""
    }
    val errorMessage: LiveData<String> = _errorMessage

    fun clearError() {
        _errorMessage.value = ""
    }

    private val _historicalData = MutableLiveData<List<ExchangeRate>>()

    val historicalData: LiveData<List<ExchangeRate>> = _historicalData

    fun fetchHistoricalTimeSeriesRates(
        baseCurrency: String,
        destinationCurrency: String
    ) {
        // No flag to prevent re-fetch since here not needed
        val endDate = LocalDate.now().minusDays(1)
        val startDate = when(_selectedTimeSpan.value) {
            TimeSpan.HOURS_24 -> { LocalDate.now().minusDays(1) }
            TimeSpan.HOURS_48 -> { LocalDate.now().minusDays(2) }
            TimeSpan.DAYS_7 -> { LocalDate.now().minusDays(7) }
            TimeSpan.DAYS_30 -> { LocalDate.now().minusDays(30) }
            null -> LocalDate.now()
        }

        viewModelScope.launch {
            when (val result = fetchHistoricalTimeSeriesUseCase(baseCurrency,destinationCurrency,startDate,endDate)) {
                is Result.Success -> _historicalData.value = result.data.sortedByDescending { it.date }
                is Result.Error -> _errorMessage.value = result.message
            }
        }
    }

}