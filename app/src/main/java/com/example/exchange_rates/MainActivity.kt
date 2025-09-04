package com.example.exchange_rates

import androidx.navigation.fragment.fragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.exchange_rates.dataSources.ExchangeRatesDataSource
import com.example.exchange_rates.databinding.ActivityMainBinding
import com.example.exchange_rates.repositories.ExchangeRatesRepository
import com.example.exchange_rates.ui.dashboard.DashboardFragment
import com.example.exchange_rates.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setup the navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // Setup the Navigation Graph through XML layout
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph)
    }
}