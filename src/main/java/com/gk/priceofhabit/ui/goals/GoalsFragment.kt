package com.gk.priceofhabit.ui.goals

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gk.priceofhabit.data.AppDatabase
import com.gk.priceofhabit.databinding.DialogAddGoalBinding
import com.gk.priceofhabit.databinding.FragmentGoalsBinding
import com.gk.priceofhabit.entity.Goal
import com.gk.priceofhabit.ui.AddItemHandler
import kotlinx.coroutines.launch

class GoalsFragment : Fragment(), AddItemHandler {

    private lateinit var binding: FragmentGoalsBinding
    private lateinit var adapter: GoalsAdapter
    private lateinit var dao: com.gk.priceofhabit.data.GoalDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoalsBinding.inflate(inflater, container, false)

        dao = AppDatabase.getInstance(requireContext()).goalDao()

        adapter = GoalsAdapter { goal ->
            lifecycleScope.launch { dao.delete(goal) }
        }

        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGoals.adapter = adapter

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
                    .setTitle("Удалить цель?")
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

        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvGoals)
    }

    override fun onAddItem() {
        val dialogBinding = DialogAddGoalBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить цель")
            .setView(dialogBinding.root)
            .setPositiveButton("Добавить") { _, _ ->
                val title = dialogBinding.etGoalTitle.text.toString()
                val target = dialogBinding.etGoalTarget.text.toString().toDoubleOrNull() ?: 0.0

                lifecycleScope.launch {
                    dao.insert(Goal(title = title, target = target))
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
