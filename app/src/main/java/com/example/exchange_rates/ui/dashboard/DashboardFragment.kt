package com.example.exchange_rates.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchange_rates.NavArgs
import com.example.exchange_rates.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var selectedCurrency: String? = null
    private var baseCurrency: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedCurrency = it.getString(NavArgs.SELECTED_CURRENCY)
            baseCurrency = it.getString(NavArgs.BASE_CURRENCY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        val dropdown: Spinner = binding.timespan
        val adapterItems = arrayOf("24 hours","48 hours","7 days","30 days")
        val timeSpanAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, adapterItems)
        val initialPosition = adapterItems.indexOf(dashboardViewModel.selectedTimeSpan.value)
        dropdown.adapter = timeSpanAdapter
        dropdown.setSelection(initialPosition)

        val adapter = TimeSpanListAdapter(listOf("$baseCurrency -> $selectedCurrency"))
        binding.itemsRecyclerViewTimeSpan.layoutManager = LinearLayoutManager(requireContext())
        binding.itemsRecyclerViewTimeSpan.adapter = adapter
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