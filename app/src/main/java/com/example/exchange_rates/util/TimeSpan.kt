package com.example.exchange_rates.util

enum class TimeSpan(val displayName: String) {
    HOURS_24("24 hours"),
    HOURS_48("48 hours"),
    DAYS_7("7 days"),
    DAYS_30("30 days");

    override fun toString(): String = displayName

    companion object {
        fun fromDisplayName(name: String): TimeSpan? {
            return values().find { it.displayName == name }
        }
    }
}