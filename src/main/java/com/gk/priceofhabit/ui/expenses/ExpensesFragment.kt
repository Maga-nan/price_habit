package com.gk.priceofhabit.ui.expenses

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gk.priceofhabit.data.AppDatabase
import com.gk.priceofhabit.databinding.DialogAddExpenseBinding
import com.gk.priceofhabit.databinding.FragmentExpensesBinding
import com.gk.priceofhabit.entity.Expense
import com.gk.priceofhabit.ui.AddItemHandler
import kotlinx.coroutines.launch

class ExpensesFragment : Fragment(), AddItemHandler {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExpensesAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        adapter = ExpensesAdapter { expense ->
            lifecycleScope.launch { db.expenseDao().delete(expense) }
        }

        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        setupSwipeToDelete()
        observeExpenses()
    }

    private fun setupSwipeToDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.getItem(position)

                AlertDialog.Builder(requireContext())
                    .setTitle("Удалить запись?")
                    .setMessage("Удалить расход '${item.title}'?")
                    .setPositiveButton("Удалить") { _, _ ->
                        adapter.deleteItem(position)   // удаление и БД, и адаптер
                    }
                    .setNegativeButton("Отмена") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        }

        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvExpenses)
    }

    private fun observeExpenses() {
        lifecycleScope.launch {
            db.expenseDao().getAll().collect { list ->
                adapter.submitList(list)
            }
        }
    }

    override fun onAddItem() {
        val dialogBinding = DialogAddExpenseBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить расход")
            .setView(dialogBinding.root)
            .setPositiveButton("Добавить") { _, _ ->
                val title = dialogBinding.etExpenseTitle.text.toString()
                val amount = dialogBinding.etExpenseAmount.text
                    .toString().toDoubleOrNull() ?: 0.0

                lifecycleScope.launch {
                    db.expenseDao().insert(
                        Expense(title = title, amount = amount)
                    )
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
