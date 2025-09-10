package com.example.exchange_rates.presentation.ui.dashboardCompose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.exchange_rates.R
import com.example.exchange_rates.presentation.ui.common.ErrorAlert
import com.example.exchange_rates.presentation.ui.common.Table
import com.example.exchange_rates.presentation.ui.common.TableInfo
import com.example.exchange_rates.util.TimeSpan

@Composable
fun DashBoardScreen(viewModel: DashboardViewModelCompose = hiltViewModel(), navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEffect = viewModel.uiEffectFlow

    LaunchedEffect(uiEffect) {
        uiEffect.collect { effect ->
            when (effect) {
                is DashBoardUiEffect.UpdateHistoricalData -> viewModel.fetchHistoricalTimeSeriesRates(
                    effect.baseCurrency,
                    effect.destinationCurrency,
                    effect.timeSpan
                )

                is DashBoardUiEffect.ClearError -> viewModel.clearError()
            }
        }
    }

    DashBoardContent(
        state = uiState,
        onEvent = viewModel::onEvent,
        navController = navController
    )
}

@Composable
fun DashBoardContent(
    viewModel: DashboardViewModelCompose = hiltViewModel(),
    state: DashBoardUiState, onEvent: (DashBoardUiEvent) -> Unit,
    navController: NavController
) {
    var isExpanded by remember { mutableStateOf(false) }

    ErrorAlert(
        state.errorMessage,
        onEvent = onEvent,
        clearErrorCallBack = { DashBoardUiEvent.ClearError() }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = "Select the Timespan:",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Text(text = stringResource(R.string.dashboard_description), color = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Box {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = true },
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.selectedTimeSpan.toString(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }

                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                DropdownMenu(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    Box(modifier = Modifier.size(width = screenWidth, height = 190.dp)) {
                        LazyColumn {
                            items(TimeSpan.entries) { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item.toString()) },
                                    onClick = {
                                        isExpanded = false
                                        onEvent(
                                            DashBoardUiEvent.SelectTimeSpan(
                                                baseCurrency = viewModel.baseCurrency,
                                                destinationCurrency = viewModel.destinationCurrency,
                                                timeSpan = item
                                            )
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Table(
                TableInfo(
                    baseCurrencyColumnTitle = "Base Currency",
                    destinationCurrencyColumnTitle = "Destination Currency",
                    exchangeRateColumnTitle = "Exchange Rate",
                    icon = Icons.Default.CalendarMonth,
                    baseCurrencyColumnWeight = .24f,
                    destinationCurrencyColumnWeight = .21f,
                    exchangeRateColumnWeight = .25f,
                    column4Weight = .3f,
                    data = state.historicalData
                ),
                onEvent = onEvent)
        }

        FloatingActionButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Go Back"
            )
        }
    }
}