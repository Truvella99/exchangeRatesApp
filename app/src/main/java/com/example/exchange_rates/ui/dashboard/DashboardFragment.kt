package com.example.exchange_rates.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchange_rates.NavArgs
import com.example.exchange_rates.R as projectR
import com.example.exchange_rates.databinding.FragmentDashboardBinding
import com.example.exchange_rates.ui.util.TimeSpan
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var errorSnackBar: MaterialCardView
    private lateinit var errorSnackBarText: TextView
    private lateinit var closeSnackBar: ImageView

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
        errorSnackBar = view.findViewById(projectR.id.errorSnackbar)
        errorSnackBarText = view.findViewById(projectR.id.errorSnackbarText)
        closeSnackBar = view.findViewById(projectR.id.closeSnackbar)

        closeSnackBar.setOnClickListener {
            hideSnackBar()
            dashboardViewModel.clearError()
        }

        // Observe error messages
        dashboardViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
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
            dashboardViewModel.clearError()
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