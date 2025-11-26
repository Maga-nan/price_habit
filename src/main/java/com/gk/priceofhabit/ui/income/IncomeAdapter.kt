package com.gk.priceofhabit.ui.income

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gk.priceofhabit.databinding.ItemIncomeBinding
import com.gk.priceofhabit.entity.Income

class IncomeAdapter(
    private val onDeleteClick: (Income) -> Unit
) : RecyclerView.Adapter<IncomeAdapter.Holder>() {

    private val items = mutableListOf<Income>()

    fun submitList(newList: List<Income>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        val item = items[position]
        onDeleteClick(item)
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int): Income = items[position]

    inner class Holder(val binding: ItemIncomeBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Income) {
            binding.tvIncomeTitle.text = item.title
            binding.tvIncomeAmount.text = item.amount.toString()

            binding.btnDelete.setOnClickListener {
                deleteItem(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
