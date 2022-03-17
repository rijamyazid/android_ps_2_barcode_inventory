package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderCartBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartHolder

class CartAdapter(private val itemUnitPos: (Int, Int, Boolean) -> Unit) :
    RecyclerView.Adapter<CartAdapter.OrderViewHolder>() {

    private val orderList = mutableListOf<CartHolder>()
    private lateinit var adapter2 : CartAdapter2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemHolderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order, position)
    }

    override fun getItemCount() = orderList.size

    fun addOrder(orders: List<CartHolder>) {
        orderList.clear()
        orderList.addAll(orders)
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(private val binding: ItemHolderCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: CartHolder, position: Int) {
            binding.tvItemNameItem.text = order.productName
            binding.tvItemIdItem.text = order.productId

            adapter2 = CartAdapter2 { unitPos, isIncreased ->
                itemUnitPos(position, unitPos, isIncreased)
            }

            binding.rvCart2.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = adapter2
            }

            adapter2.addItemUnits(order.productUnits)
        }

    }

}