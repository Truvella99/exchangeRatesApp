package com.example.exchange_rates.presentation.ui.homeCompose

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.exchange_rates.NavArgs
import com.example.exchange_rates.R
import com.example.exchange_rates.presentation.ui.common.Table
import com.example.exchange_rates.presentation.ui.common.TableInfo

@Composable
fun HomeScreen(viewModel: HomeViewModelCompose = hiltViewModel(), navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEffect = viewModel.uiEffectFlow

    LaunchedEffect(uiEffect) {
        uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.NavigateToDetail -> {
                    val bundle = bundleOf(
                        NavArgs.SELECTED_CURRENCY to effect.destinationCurrency,
                        NavArgs.BASE_CURRENCY to effect.baseCurrency
                    )
                    navController.navigate(R.id.action_homeFragment_to_dashboardFragment_Compose, bundle)
                }
                is HomeUiEffect.FetchNewExchangeRates -> viewModel.fetchNewExchangeRates(effect.selectedCurrency)
                is HomeUiEffect.UpdateTab -> viewModel.updateTab(effect.index)
                is HomeUiEffect.UpdateFavourites -> viewModel.toggleFavouriteCurrency(effect.currency)
            }
        }
    }

    HomeContent(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(state: HomeUiState, onEvent: (HomeUiEvent) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Select Currency:",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(R.string.home_description), color = Color.Gray)
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
                        text = state.selectedCurrency.ifEmpty { "Select Currency" },
                        color = if (state.selectedCurrency.isEmpty())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                Box(modifier = Modifier.size(width = 250.dp, height = 350.dp)) {
                    LazyColumn {
                        items(state.currencies) { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    isExpanded = false
                                    onEvent(HomeUiEvent.SelectCurrency(item))
                                },
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        HomeTab(state, onEvent)
        Spacer(Modifier.height(16.dp))
        Table(
            TableInfo(
                column1Title = "Base Currency",
                column2Title = "Destination Currency",
                column3Title = "Exchange Rate",
                icon = Icons.Default.Star,
                column1Weight = .25f,
                column2Weight = .3f,
                column3Weight = .25f,
                column4Weight = .2f,
                data = if (state.selectedTabIndex == 0)
                    state.exchangeRates.filter { !it.value }.keys.toList()
                else
                    state.exchangeRates.filter { it.value }.keys.toList(),
                isDataFavourite = if (state.selectedTabIndex == 0)
                    state.exchangeRates.filter { !it.value }.values.toList()
                else
                    state.exchangeRates.filter { it.value }.values.toList(),
            ), onEvent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(state: HomeUiState, onEvent: (HomeUiEvent) -> Unit) {
    val tabs = listOf<Pair<String, @Composable () -> Unit>>(
        "CURRENCIES" to { Icon(Icons.Default.Search, contentDescription = "Search") },
        "FAVOURITES" to { Icon(Icons.Default.Star, contentDescription = "Star") }
    )
    PrimaryTabRow(selectedTabIndex = state.selectedTabIndex) {
        tabs.forEachIndexed { index, (title, icon) ->
            Tab(
                selected = state.selectedTabIndex == index,
                onClick = {
                    onEvent(HomeUiEvent.SelectTab(index))
                },
                icon = icon,
                text = {
                    Text(
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}