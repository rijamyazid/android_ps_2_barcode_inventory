package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderOrderItemBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderHolder
import com.rmyfactory.rmyinventorybarcode.util.Functions.dotPriceIND

class OrderAdapter(private val onclickQty: (Int, Boolean) -> Unit) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val orderList = mutableListOf<OrderHolder>()

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

    fun addOrder(orders: List<OrderHolder>) {
        orderList.clear()
        orderList.addAll(orders)
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(private val binding: ItemHolderOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderHolder, position: Int) {
            try {
                binding.tvItemNameItem.text = order.itemName
                binding.tvItemIdItem.text = order.itemId
                binding.tvItemPriceItem.text = dotPriceIND(order.itemPrice)
                binding.tvItemQtyItem.text = order.itemQty.toString()
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