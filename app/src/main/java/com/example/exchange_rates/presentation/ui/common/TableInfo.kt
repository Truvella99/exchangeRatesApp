package com.example.exchange_rates.presentation.ui.common
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.exchange_rates.domain.model.ExchangeRate

data class TableInfo(
    val column1Title : String,
    val column2Title : String,
    val column3Title : String,
    val icon: ImageVector,
    val column1Weight: Float,
    val column2Weight: Float,
    val column3Weight: Float,
    val column4Weight: Float,
    val data: List<ExchangeRate>,
    val isDataFavourite: List<Boolean>? = null
)
