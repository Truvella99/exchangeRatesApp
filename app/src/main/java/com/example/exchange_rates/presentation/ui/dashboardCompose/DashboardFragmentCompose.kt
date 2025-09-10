package com.example.exchange_rates.presentation.ui.dashboardCompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.exchange_rates.R as projectR
import com.example.exchange_rates.presentation.ui.homeCompose.HomeScreen
import com.example.exchange_rates.presentation.ui.theme.MyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragmentCompose : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(projectR.layout.fragment_home_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ComposeView>(projectR.id.fragmentHomeCompose).setContent {
            MyAppTheme {
                Text("dee")
            }
        }
    }
}