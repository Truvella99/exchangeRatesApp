package com.example.exchange_rates.ui.home

import android.R
import com.example.exchange_rates.R as projectR
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchange_rates.NavArgs
import com.example.exchange_rates.dataModels.ExchangeRate
import com.example.exchange_rates.dataSources.ExchangeRatesDataSource
import com.example.exchange_rates.databinding.FragmentHomeBinding
import com.example.exchange_rates.repositories.ExchangeRatesRepository
import com.example.exchange_rates.useCases.FetchHistoricalTimeSeriesUseCase
import com.example.exchange_rates.useCases.FetchLatestExchangeUseCase
import com.example.exchange_rates.useCases.GetCurrenciesUseCase
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // fetch the data
        homeViewModel.fetchLatestExchangeRates()
        homeViewModel.getAllCurrencies()
        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get the spinner from the xml.
        homeViewModel.currencies.observe(viewLifecycleOwner) { currencies ->
            val dropdown: Spinner = binding.menu
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, currencies)
            dropdown.adapter = adapter

            // Optionally set selection if you want
            val selected = homeViewModel.selectedCurrency.value
            val initialPosition = currencies.indexOf(selected)
            if (initialPosition >= 0) {
                dropdown.setSelection(initialPosition)
            }

            dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    homeViewModel.setCurrency(selectedItem)
                    homeViewModel.fetchLatestExchangeRates(true)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        val tabLayout: TabLayout = binding.simpleTabLayout
        val firstTab: TabLayout.Tab = tabLayout.newTab() // Create a new Tab names
        firstTab.text = "Favourites" // set the Text for the first Tab
        firstTab.setIcon(R.drawable.star_on)
        tabLayout.addTab(firstTab,true)
        val secondTab: TabLayout.Tab = tabLayout.newTab() // Create a new Tab names
        secondTab.text = "Others" // set the Text for the second Tab
        secondTab.setIcon(R.drawable.ic_menu_search)
        tabLayout.addTab(secondTab)

        // Setup tab selection listener to notify ViewModel when user taps a tab
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                homeViewModel.selectTab(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Observe currencies LiveData
        homeViewModel.exchangeRates.observe(viewLifecycleOwner) { currencyMap ->
            // Log.d("HomeFragment", "Currencies updated: $currencyMap")
            val tabsAdapter = HomeListAdapter(
                // start to tab 0 (favourites) so only true items
                items = currencyMap.keys.toList(),
                favourites = currencyMap,
                onFavouriteToggle = { currency ->
                    homeViewModel.toggleFavouriteCurrency(currency)
                },
                onItemClick = { currency ->
                    // Navigate to the Dashboard Fragment
                    val bundle = bundleOf(
                        NavArgs.SELECTED_CURRENCY to currency.destinationCurrency,
                        NavArgs.BASE_CURRENCY to homeViewModel.selectedCurrency.value
                    )

                    val navController = view.findNavController()
                    navController.navigate(projectR.id.action_homeFragment_to_dashboardFragment2, bundle)
                }
            )
            binding.itemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.itemsRecyclerView.adapter = tabsAdapter

            // Observe the selected tab state from ViewModel
            homeViewModel.selectedTabIndex.observe(viewLifecycleOwner) { selectedIndex ->
                if (selectedIndex != tabLayout.selectedTabPosition) {
                    val tab = tabLayout.getTabAt(selectedIndex)
                    tab?.select()
                }
                // Update adapter data based on selected tab
                tabsAdapter.updateItems(selectedIndex)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}