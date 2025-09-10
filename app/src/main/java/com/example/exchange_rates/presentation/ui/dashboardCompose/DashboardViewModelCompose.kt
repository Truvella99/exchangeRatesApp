package com.example.exchange_rates.presentation.ui.dashboardCompose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.util.Result
import com.example.exchange_rates.util.TimeSpan
import com.example.exchange_rates.domain.usecases.FetchHistoricalTimeSeriesUseCase
import com.example.exchange_rates.presentation.ui.dashboardCompose.DashBoardUiEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModelCompose @Inject constructor(
    private val fetchHistoricalTimeSeriesUseCase: FetchHistoricalTimeSeriesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashBoardUiState())
    val uiState: StateFlow<DashBoardUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<DashBoardUiEffect>()
    val uiEffectFlow = _uiEffect.asSharedFlow()

    val destinationCurrency: String = savedStateHandle["selected_currency"] ?: ""
    val baseCurrency: String = savedStateHandle["base_currency"] ?: ""

    init {
        viewModelScope.launch {
            val endDate = LocalDate.now().minusDays(1)
            val startDate = when(_uiState.value.selectedTimeSpan) {
                TimeSpan.HOURS_24 -> { LocalDate.now().minusDays(1) }
                TimeSpan.HOURS_48 -> { LocalDate.now().minusDays(2) }
                TimeSpan.DAYS_7 -> { LocalDate.now().minusDays(7) }
                TimeSpan.DAYS_30 -> { LocalDate.now().minusDays(30) }
            }

            when (val result = fetchHistoricalTimeSeriesUseCase(
                baseCurrency,
                destinationCurrency,
                startDate,
                endDate
            )) {
                is Result.Success -> _uiState.update { it.copy(historicalData = result.data.sortedByDescending { it.date }) }
                is Result.Error -> _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun onEvent(event: DashBoardUiEvent) {
        viewModelScope.launch {
            when(event) {
                is DashBoardUiEvent.SelectTimeSpan ->
                    _uiEffect.emit(
                        UpdateHistoricalData(
                            event.baseCurrency,
                            event.destinationCurrency,
                            event.timeSpan
                        )
                    )

                is DashBoardUiEvent.ClearError ->
                    _uiEffect.emit(DashBoardUiEffect.ClearError())
            }
        }
    }

    suspend fun fetchHistoricalTimeSeriesRates(
        baseCurrency: String,
        destinationCurrency: String,
        timeSpan: TimeSpan
    ) {
        _uiState.update { it.copy(selectedTimeSpan = timeSpan) }
        // No flag to prevent re-fetch since here not needed
        val endDate = LocalDate.now().minusDays(1)
        val startDate = when(timeSpan) {
            TimeSpan.HOURS_24 -> { LocalDate.now().minusDays(1) }
            TimeSpan.HOURS_48 -> { LocalDate.now().minusDays(2) }
            TimeSpan.DAYS_7 -> { LocalDate.now().minusDays(7) }
            TimeSpan.DAYS_30 -> { LocalDate.now().minusDays(30) }
        }

        when (val result = fetchHistoricalTimeSeriesUseCase(
            baseCurrency,
            destinationCurrency,
            startDate,
            endDate
        )) {
            is Result.Success -> _uiState.update { it.copy(historicalData = result.data.sortedByDescending { it.date }) }
            is Result.Error -> _uiState.update { it.copy(errorMessage = result.message) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }
}