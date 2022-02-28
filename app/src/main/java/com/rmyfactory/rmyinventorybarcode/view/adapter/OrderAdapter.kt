package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderOrderItemBinding

class OrderAdapter(private val onclickQty: (Int, Boolean) -> Unit) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val orderList = mutableListOf<MutableMap<String, String>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemHolderOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order, position)
    }

    override fun getItemCount() = orderList.size

    fun addOrder(orders: List<MutableMap<String, String>>) {
        orderList.clear()
        orderList.addAll(orders)
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(private val binding: ItemHolderOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Map<String, String>, position: Int) {
            try {
                binding.tvItemNameItem.text = order["orderName"]
                binding.tvItemIdItem.text = order["orderId"]
                binding.tvItemPriceItem.text = order["orderPrice"]
                binding.tvItemQtyItem.text = order["orderQty"]
            } catch (e: Exception) {

            }

            binding.imgIncreaseQty.setOnClickListener {
                onclickQty(position, true)
                binding.tvItemQtyItem.text =
                    (binding.tvItemQtyItem.text.toString().toInt() + 1).toString()
            }

            binding.imgDecreaseQty.setOnClickListener {
                if (binding.tvItemQtyItem.text.toString().toInt() > 1) {
                    onclickQty(position, false)
                    binding.tvItemQtyItem.text =
                        (binding.tvItemQtyItem.text.toString().toInt() - 1).toString()
                }
            }

        }

    }

}