package com.gk.priceofhabit.ui.analytics

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.gk.priceofhabit.data.AppDatabase
import com.gk.priceofhabit.databinding.FragmentAnalyticsBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())

        // <<< Настройка графика - запрещаем отрицательные значения
        binding.chartAccumulation.axisLeft.axisMinimum = 0f
        binding.chartAccumulation.axisRight.isEnabled = false

        lifecycleScope.launch {

            combine(
                db.incomeDao().getAll(),    // доходы
                db.expenseDao().getAll()    // расходы
            ) { incomeList, expenseList ->

                val events = mutableListOf<Pair<Int, Double>>()

                incomeList.forEach {
                    events.add(events.size to it.amount)
                }
                expenseList.forEach {
                    events.add(events.size to -it.amount)
                }

                // ----------- Проверка на перерасход ------------
                val totalIncome = incomeList.sumOf { it.amount }
                val totalExpenses = expenseList.sumOf { it.amount }

                if (totalExpenses > totalIncome) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Перерасход!")
                        .setMessage("Ваши расходы превышают доходы.")
                        .setPositiveButton("OK", null)
                        .show()
                }

                // ----------- Накопительная линия --------------
                var total = 0.0
                var index = 0f

                val entries = events.map { event ->
                    total += event.second
                    Entry(index++, total.toFloat())
                }

                entries
            }.collect { entries ->

                val dataSet = LineDataSet(entries, "Накопление").apply {
                    color = Color.BLUE
                    setCircleColor(Color.RED)
                    lineWidth = 2f
                    circleRadius = 4f
                }

                binding.chartAccumulation.data = LineData(dataSet)
                binding.chartAccumulation.invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
