package com.example.exchange_rates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.exchange_rates.presentation.home.HomeViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class HomeViewModelUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val homeViewModel = HomeViewModel()

    @Test
    fun `selectTab with valid index 0 sets selectedTabIndex to 0`() {
        // Act
        homeViewModel.selectTab(0)

        // Assert
        assertEquals(0, homeViewModel.selectedTabIndex.value)
    }

    @Test
    fun `selectTab with valid index 1 sets selectedTabIndex to 0`() {
        // Act
        homeViewModel.selectTab(1)

        // Assert
        assertEquals(1, homeViewModel.selectedTabIndex.value)
    }

    @Test
    fun `selectTab with invalid positive index sets selectedTabIndex to 0`() {
        // Act
        homeViewModel.selectTab(2)

        // Assert
        assertEquals(0, homeViewModel.selectedTabIndex.value)
    }

    @Test
    fun `selectTab with invalid negative index sets selectedTabIndex to 0`() {
        // Act
        homeViewModel.selectTab(-2)

        // Assert
        assertEquals(0, homeViewModel.selectedTabIndex.value)
    }
}