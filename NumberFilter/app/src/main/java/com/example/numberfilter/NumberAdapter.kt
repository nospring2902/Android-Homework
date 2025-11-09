package com.example.numberfilter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.numberfilter.databinding.ItemNumberBinding

class NumberAdapter : RecyclerView.Adapter<NumberAdapter.VH>() {
    private val data = mutableListOf<Int>()

    fun submit(list: List<Int>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    class VH(val binding: ItemNumberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        return VH(ItemNumberBinding.inflate(inf, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.tvNumber.text = data[position].toString()
    }
}
