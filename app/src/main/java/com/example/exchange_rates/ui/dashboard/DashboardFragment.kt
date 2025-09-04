package com.example.exchange_rates.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchange_rates.NavArgs
import com.example.exchange_rates.databinding.FragmentDashboardBinding
import com.example.exchange_rates.ui.util.TimeSpan
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dashboardViewModel : DashboardViewModel by viewModels()

    private lateinit var selectedCurrency: String
    private lateinit var baseCurrency: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedCurrency = it.getString(NavArgs.SELECTED_CURRENCY)!!
            baseCurrency = it.getString(NavArgs.BASE_CURRENCY)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // retrieve the data
        dashboardViewModel.fetchHistoricalTimeSeriesRates(baseCurrency,selectedCurrency)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dropdown: Spinner = binding.timespan
        val adapterItems = TimeSpan.entries
        val timeSpanAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, adapterItems)
        val initialPosition = adapterItems.indexOf(dashboardViewModel.selectedTimeSpan.value)
        dropdown.adapter = timeSpanAdapter
        dropdown.setSelection(initialPosition)

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                dashboardViewModel.setTimeSpan(selectedItem)
                dashboardViewModel.fetchHistoricalTimeSeriesRates(baseCurrency,selectedCurrency)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        dashboardViewModel.historicalData.observe(viewLifecycleOwner) { historicalData ->
            val adapter = TimeSpanListAdapter(historicalData)
            context?.let { ctx ->
                binding.itemsRecyclerViewTimeSpan.layoutManager = LinearLayoutManager(ctx)
                binding.itemsRecyclerViewTimeSpan.adapter = adapter
            }
        }

        // go back button
        binding.fabBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}