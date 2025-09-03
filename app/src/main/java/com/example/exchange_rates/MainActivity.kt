package com.example.exchange_rates

import androidx.navigation.fragment.fragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.exchange_rates.databinding.ActivityMainBinding
import com.example.exchange_rates.ui.dashboard.DashboardFragment
import com.example.exchange_rates.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }*/

        // Setup the navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup the Navigation Graph
        // Add the graph to the NavController with `createGraph()`.
        navController.graph = navController.createGraph(
            startDestination = "home"
        ) {
            fragment<HomeFragment>("home") {
                label = "Home"
            }

            fragment<DashboardFragment>("dashboard") {
                label = "Dashboard"
                argument(NavArgs.SELECTED_CURRENCY) {
                    type = NavType.StringType
                    defaultValue = "EUR"
                }
                argument(NavArgs.BASE_CURRENCY) {
                    type = NavType.StringType
                    defaultValue = "EUR"
                }
            }
        }
    }
}