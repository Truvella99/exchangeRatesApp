package com.example.exchange_rates.presentation.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.exchange_rates.domain.model.ExchangeRate
import com.example.exchange_rates.presentation.ui.dashboardCompose.DashBoardUiEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun<T> Table(
    info: TableInfo, onEvent: (T) -> Unit,
    // Nullable Callbacks (defined for home events, null for Dashboard Since no Events
    navigateCallBack: ((ExchangeRate) -> T)? = null,
    setAsFavouriteCallBack: ((ExchangeRate) -> T)? = null
) {
    LazyColumn(Modifier.fillMaxSize()) {
        // Header
        stickyHeader {
            Row(
                Modifier.background(Color.LightGray).fillMaxWidth().height(IntrinsicSize.Min).border(1.dp, Color.Black),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text( text = info.baseCurrencyColumnTitle,
                    Modifier
                        .weight(info.baseCurrencyColumnWeight)
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    thickness = DividerDefaults.Thickness, color = Color.Black
                )
                Text( text = info.destinationCurrencyColumnTitle,
                    Modifier
                        .weight(info.destinationCurrencyColumnWeight)
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    thickness = DividerDefaults.Thickness, color = Color.Black
                )
                Text( text = info.exchangeRateColumnTitle,
                    Modifier
                        .weight(info.exchangeRateColumnWeight)
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = Color.Black
                )
                Box(
                    modifier = Modifier
                        .weight(info.column4Weight)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = info.icon,
                        modifier = Modifier.size(35.dp),
                        contentDescription = null
                    )
                }
            }
        }
        // Table Items
        itemsIndexed(info.data) { index, item ->
            if (info.isDataFavourite != null) {
                // Home Page where Favourites Data are Present
                TableItem(
                    item,
                    info.baseCurrencyColumnWeight,
                    info.destinationCurrencyColumnWeight,
                    info.exchangeRateColumnWeight,
                    info.column4Weight,
                    info.isDataFavourite[index],
                    navigateCallBack = navigateCallBack?.let { cb -> { cb(item)} },
                    setAsFavouriteCallBack = setAsFavouriteCallBack?.let { cb -> { cb(item)} },
                    onEvent = onEvent
                )
            } else {
                // DashBoard Page Where there are no Favourites
                TableItem<DashBoardUiEvent>(
                    item,
                    info.baseCurrencyColumnWeight,
                    info.destinationCurrencyColumnWeight,
                    info.exchangeRateColumnWeight,
                    info.column4Weight
                )
            }
        }
    }
}

@Composable
fun<T> TableItem(item: ExchangeRate,
              column1Weight: Float,
              column2Weight: Float,
              column3Weight: Float,
              column4Weight: Float,
              // Nullable Callbacks (defined for home events, null for Dashboard Since no Events
              isFavourite: Boolean? = null,
              navigateCallBack: (() -> T)? = null,
              setAsFavouriteCallBack: (() -> T)? = null,
              onEvent: ((T) -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().height(IntrinsicSize.Min).clickable(
            enabled = isFavourite != null,
            onClick = {
            navigateCallBack?.let { navigateCallBack ->
                onEvent?.invoke(navigateCallBack())
            }
        }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text( text = item.baseCurrency,
            Modifier
                .weight(column1Weight)
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
        Text( text = item.destinationCurrency,
            Modifier
                .weight(column2Weight)
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
        Text(text = item.exchangeRate.toString(),
            Modifier
                .weight(column3Weight)
                .padding(8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Box(
            modifier = Modifier
                .weight(column4Weight)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (setAsFavouriteCallBack != null) {
                Icon(
                    modifier = Modifier.clickable(
                        onClick = {
                            onEvent?.invoke(setAsFavouriteCallBack())
                        }
                    ),
                    imageVector = if (isFavourite == true) Icons.Default.Star else Icons.Default.StarBorder,
                    tint = if (isFavourite == true) Color(0xFFFFC107) else Color.Black,
                    contentDescription = null
                )
            } else {
                Text(item.date.toString())
            }
        }
    }
}