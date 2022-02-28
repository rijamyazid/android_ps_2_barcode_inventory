package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderOrderItemBinding

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val orderList = mutableListOf<Map<String, String>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemHolderOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount() = orderList.size

    fun addOrder(orders: List<Map<String, String>>) {
        orderList.clear()
        orderList.addAll(orders)
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(private val binding: ItemHolderOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Map<String, String>) {
            try {
                binding.tvItemNameItem.text = order["orderName"]
                binding.tvItemIdItem.text = order["orderId"]
                binding.tvItemPriceItem.text = order["orderPrice"]
                binding.tvItemQtyItem.text = order["orderQty"]
            } catch (e: Exception) {

            }

            binding.imgIncreaseQty.setOnClickListener {
                binding.tvItemQtyItem.text =
                    (binding.tvItemQtyItem.text.toString().toInt() + 1).toString()
            }

            binding.imgDecreaseQty.setOnClickListener {
                if (binding.tvItemQtyItem.text.toString().toInt() > 1) {
                    binding.tvItemQtyItem.text =
                        (binding.tvItemQtyItem.text.toString().toInt() - 1).toString()
                }
            }

        }

    }

}