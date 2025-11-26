package com.gk.priceofhabit.ui.expenses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gk.priceofhabit.databinding.ItemExpenseBinding
import com.gk.priceofhabit.entity.Expense

class ExpensesAdapter(
    private val onDeleteClick: (Expense) -> Unit   // callback удаления
) : RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {

    private val items = mutableListOf<Expense>()   // теперь removeAt работает

    fun deleteItem(position: Int) {
        val item = items[position]
        onDeleteClick(item)            // удаляем из базы
        items.removeAt(position)       // удаляем из списка
        notifyItemRemoved(position)
    }

    fun submitList(newList: List<Expense>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Expense = items[position]

    class ExpenseViewHolder(val binding: ItemExpenseBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvAmount.text = item.amount.toString()

        // Кнопка удаления
        holder.binding.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = items.size
}
