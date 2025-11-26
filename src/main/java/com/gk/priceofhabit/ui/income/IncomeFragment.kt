package com.gk.priceofhabit.ui.income

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gk.priceofhabit.data.AppDatabase
import com.gk.priceofhabit.databinding.DialogAddIncomeBinding
import com.gk.priceofhabit.databinding.FragmentIncomeBinding
import com.gk.priceofhabit.entity.Income
import com.gk.priceofhabit.ui.AddItemHandler
import kotlinx.coroutines.launch

class IncomeFragment : Fragment(), AddItemHandler {

    private lateinit var binding: FragmentIncomeBinding
    private lateinit var adapter: IncomeAdapter
    private lateinit var dao: com.gk.priceofhabit.data.IncomeDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentIncomeBinding.inflate(inflater, container, false)

        dao = AppDatabase.getInstance(requireContext()).incomeDao()

        adapter = IncomeAdapter { income ->
            lifecycleScope.launch { dao.delete(income) }
        }

        binding.rvIncome.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIncome.adapter = adapter

        setupSwipeDelete()
        observeData()

        return binding.root
    }

    private fun observeData() {
        lifecycleScope.launch {
            dao.getAll().collect { adapter.submitList(it) }
        }
    }

    private fun setupSwipeDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val position = vh.adapterPosition
                val item = adapter.getItem(position)

                AlertDialog.Builder(requireContext())
                    .setTitle("Удалить доход?")
                    .setMessage("Удалить '${item.title}'?")
                    .setPositiveButton("Удалить") { _, _ ->
                        adapter.deleteItem(position)
                    }
                    .setNegativeButton("Отмена") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        }

        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvIncome)
    }

    override fun onAddItem() {
        val dialogBinding = DialogAddIncomeBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить доход")
            .setView(dialogBinding.root)
            .setPositiveButton("Добавить") { _, _ ->
                val title = dialogBinding.etIncomeTitle.text.toString()
                val amount = dialogBinding.etIncomeAmount.text.toString().toDoubleOrNull() ?: 0.0

                lifecycleScope.launch {
                    dao.insert(Income(title = title, amount = amount))
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
