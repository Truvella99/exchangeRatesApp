package com.example.exchange_rates.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exchange_rates.repositories.ExchangeRatesRepository

class HomeViewModel() : ViewModel() {

    private val _selectedCurrency = MutableLiveData<String>().apply {
        value = "EUR"
    }
    val selectedCurrency: LiveData<String> = _selectedCurrency

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
    }

    private var _currencies = MutableLiveData<MutableMap<String, Boolean>>().apply {
        value = mutableMapOf(
            "AED" to false, "AFN" to false, "ALL" to false, "AMD" to false,
            "ANG" to false, "AOA" to false, "ARS" to false, "AUD" to false,
            "AWG" to false, "AZN" to false, "BAM" to false, "BBD" to false,
            "BDT" to false, "BGN" to false, "BHD" to false, "BIF" to false,
            "BMD" to false, "BND" to false, "BOB" to false, "BRL" to false,
            "BSD" to false, "BTN" to false, "BWP" to false, "BYR" to false,
            "BZD" to false, "CAD" to false, "CDF" to false, "CHF" to false,
            "CLP" to false, "CNY" to false, "COP" to false, "CRC" to false,
            "CUC" to false, "CUP" to false, "CVE" to false, "CZK" to false,
            "DJF" to false, "DKK" to false, "DOP" to false, "DZD" to false,
            "EGP" to false, "ERN" to false, "ETB" to false, "EUR" to false,
            "FJD" to false, "FKP" to false, "GBP" to false, "GEL" to false,
            "GGP" to false, "GHS" to false, "GIP" to false, "GMD" to false,
            "GNF" to false, "GTQ" to false, "GYD" to false, "HKD" to false,
            "HNL" to false, "HRK" to false, "HTG" to false, "HUF" to false,
            "IDR" to false, "ILS" to false, "IMP" to false, "INR" to false,
            "IQD" to false, "IRR" to false, "ISK" to false, "JEP" to false,
            "JMD" to false, "JOD" to false, "JPY" to false, "KES" to false,
            "KGS" to false, "KHR" to false, "KMF" to false, "KPW" to false,
            "KRW" to false, "KWD" to false, "KYD" to false, "KZT" to false,
            "LAK" to false, "LBP" to false, "LKR" to false, "LRD" to false,
            "LSL" to false, "LYD" to false, "MAD" to false, "MDL" to false,
            "MGA" to false, "MKD" to false, "MMK" to false, "MNT" to false,
            "MOP" to false, "MRO" to false, "MUR" to false, "MVR" to false,
            "MWK" to false, "MXN" to false, "MYR" to false, "MZN" to false,
            "NAD" to false, "NGN" to false, "NIO" to false, "NOK" to false,
            "NPR" to false, "NZD" to false, "OMR" to false, "PAB" to false,
            "PEN" to false, "PGK" to false, "PHP" to false, "PKR" to false,
            "PLN" to false, "PYG" to false, "QAR" to false, "RON" to false,
            "RSD" to false, "RUB" to false, "RWF" to false, "SAR" to false,
            "SBD" to false, "SCR" to false, "SDG" to false, "SEK" to false,
            "SGD" to false, "SHP" to false, "SLL" to false, "SOS" to false,
            "SPL" to false, "SRD" to false, "STD" to false, "SVC" to false,
            "SYP" to false, "SZL" to false, "THB" to false, "TJS" to false,
            "TMT" to false, "TND" to false, "TOP" to false, "TRY" to false,
            "TTD" to false, "TVD" to false, "TWD" to false, "TZS" to false,
            "UAH" to false, "UGX" to false, "USD" to false, "UYU" to false,
            "UZS" to false, "VEF" to false, "VND" to false, "VUV" to false,
            "WST" to false, "XAF" to false, "XCD" to false, "XDR" to false,
            "XOF" to false, "XPF" to false, "YER" to false, "ZAR" to false,
            "ZMW" to false, "ZWD" to false
        )
    }
    val currencies: LiveData<MutableMap<String, Boolean>> = _currencies

    fun toggleFavouriteCurrency(currency: String) {
        val isFavourite = _currencies.value?.get(currency)!!
        _currencies.value = _currencies.value?.toMutableMap()?.apply {
            put(currency, !isFavourite)
        }
        Log.e("HomeFragment", _currencies.value?.filter { it.value }.toString())
    }

    // 0 for Favorites, 1 for Others
    private val _selectedTabIndex = MutableLiveData(0)
    val selectedTabIndex: LiveData<Int> get() = _selectedTabIndex

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}