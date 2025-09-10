package com.example.exchange_rates.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel tied with Fragment Lifecycle scope
class HomeViewModel() : ViewModel() {

    // 0 for Favorites, 1 for Others
    private val _selectedTabIndex = MutableLiveData(0)
    val selectedTabIndex: LiveData<Int> get() = _selectedTabIndex

    fun selectTab(index: Int) {
        _selectedTabIndex.value = set(index)
    }

    private fun set(index: Int): Int {
        if (index >= 0 && index <= 1) {
            return index
        } else {
            return 0
        }
    }
}