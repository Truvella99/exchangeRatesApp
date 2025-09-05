package com.example.exchange_rates.presentation.home

import android.R
import com.example.exchange_rates.R as projectR
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchange_rates.NavArgs
import com.example.exchange_rates.databinding.FragmentHomeBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var errorSnackBar: MaterialCardView
    private lateinit var errorSnackBarText: TextView
    private lateinit var closeSnackBar: ImageView

    private val homeApiViewModel: HomeApiViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // fetch the data
        homeApiViewModel.getAllCurrencies()
        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get the spinner from the xml.
        homeApiViewModel.currencies.observe(viewLifecycleOwner) { currencies ->
            val dropdown: Spinner = binding.menu
            context?.let { ctx ->
                val adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, currencies)
                dropdown.adapter = adapter

                // Optionally set selection if you want
                val selected = homeApiViewModel.selectedCurrency.value
                val initialPosition = currencies.indexOf(selected)
                if (initialPosition >= 0) {
                    dropdown.setSelection(initialPosition)
                }

                dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        homeApiViewModel.fetchLatestExchangeRates(selectedItem)
                        homeApiViewModel.setCurrency(selectedItem)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
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
        homeApiViewModel.exchangeRates.observe(viewLifecycleOwner) { currencyMap ->
            val tabsAdapter = HomeListAdapter(
                // start to tab 0 (favourites) so only true items
                items = currencyMap.keys.toList(),
                favourites = currencyMap,
                onFavouriteToggle = { currency ->
                    homeApiViewModel.toggleFavouriteCurrency(currency)
                },
                onItemClick = { currency ->
                    // Navigate to the Dashboard Fragment
                    val bundle = bundleOf(
                        NavArgs.SELECTED_CURRENCY to currency.destinationCurrency,
                        NavArgs.BASE_CURRENCY to homeApiViewModel.selectedCurrency.value
                    )

                    val navController = view.findNavController()
                    navController.navigate(projectR.id.action_homeFragment_to_dashboardFragment2, bundle)
                }
            )
            context?.let { ctx ->
                binding.itemsRecyclerView.layoutManager = LinearLayoutManager(ctx)
                binding.itemsRecyclerView.adapter = tabsAdapter
            }

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

        errorSnackBar = view.findViewById(projectR.id.errorSnackbar)
        errorSnackBarText = view.findViewById(projectR.id.errorSnackbarText)
        closeSnackBar = view.findViewById(projectR.id.closeSnackbar)

        closeSnackBar.setOnClickListener {
            hideSnackBar()
            homeApiViewModel.clearError()
        }

        // Observe error messages
        homeApiViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrBlank()) {
                showSnackBar(errorMsg)
            }
        }
    }

    private fun showSnackBar(message: String) {
        errorSnackBarText.text = message
        errorSnackBar.visibility = View.VISIBLE

        errorSnackBar.postDelayed({
            hideSnackBar()
            homeApiViewModel.clearError()
        }, 4000)
    }

    private fun hideSnackBar() {
        errorSnackBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}