package com.example.exchange_rates.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _selectedTimeSpan = MutableLiveData<String>().apply {
        value = "24 hours"
    }
    val selectedTimeSpan: LiveData<String> = _selectedTimeSpan

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}