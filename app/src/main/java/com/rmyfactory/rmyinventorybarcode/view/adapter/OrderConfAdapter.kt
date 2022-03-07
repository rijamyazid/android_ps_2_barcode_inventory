package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderOrderConfirmationBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.OrderHolder
import com.rmyfactory.rmyinventorybarcode.util.Functions.dotPriceIND

class OrderConfAdapter(private val context: Context) :
    RecyclerView.Adapter<OrderConfAdapter.OrderConfViewHolder>() {

    private val orderList = mutableListOf<OrderHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderConfViewHolder {
        val binding = ItemHolderOrderConfirmationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderConfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderConfViewHolder, position: Int) {
        val orderHolder = orderList[position]
        holder.bind(orderHolder)
    }

    override fun getItemCount() = orderList.size

    fun addOrders(orders: List<OrderHolder>) {
        orderList.clear()
        orderList.addAll(orders)
        notifyDataSetChanged()
    }

    inner class OrderConfViewHolder(private val binding: ItemHolderOrderConfirmationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderHolder) {
            binding.tvOrderConfId.text = order.itemId
            binding.tvOrderConfName.text = order.itemName
            binding.tvOrderConfPrice.text = dotPriceIND(order.itemPrice)
            binding.tvOrderConfQty.text =
                context.getString(R.string.order_conf_qty, order.itemQty.toString())
            binding.tvOrderConfTotalPrice.text =
                dotPriceIND((order.itemPrice.toLong() * order.itemQty).toString())
        }

    }

}