package com.gk.priceofhabit.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gk.priceofhabit.databinding.ItemGoalBinding
import com.gk.priceofhabit.entity.Goal

class GoalsAdapter(
    private val onDeleteClick: (Goal) -> Unit    // callback удаления
) : RecyclerView.Adapter<GoalsAdapter.Holder>() {

    private val items = mutableListOf<Goal>()

    fun submitList(newList: List<Goal>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        val item = items[position]
        onDeleteClick(item)       // удаление из базы
        items.removeAt(position)  // удаление из списка
        notifyItemRemoved(position)
    }

    fun getItem(position: Int): Goal = items[position]

    inner class Holder(val binding: ItemGoalBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Goal) {
            binding.tvGoalTitle.text = item.title
            binding.tvGoalTarget.text = item.target.toString()

            binding.btnDelete.setOnClickListener {
                deleteItem(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemGoalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
