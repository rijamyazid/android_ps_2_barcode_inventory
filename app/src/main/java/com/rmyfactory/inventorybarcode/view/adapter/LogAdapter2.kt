package com.rmyfactory.inventorybarcode.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.ItemHolderOrderBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.with.OrderProductWithProduct
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat

class LogAdapter2(private val context:Context): RecyclerView.Adapter<LogAdapter2.ViewHolder>() {

    private val orderProducts = mutableListOf<OrderProductWithProduct>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHolderOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderProducts[position])
    }

    override fun getItemCount() = orderProducts.size

    fun submitData(orderProducts: List<OrderProductWithProduct>) {
        this.orderProducts.clear()
        this.orderProducts.addAll(orderProducts)
        notifyItemRangeInserted(0, orderProducts.size)
    }

    inner class ViewHolder(private val binding: ItemHolderOrderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderProductWithProduct) {

            binding.tvOrderConfId.text = order.orderProductModel.productId
            binding.tvOrderConfName.text = order.product.productName
            binding.tvOrderConfQty.text = context.getString(R.string.order_conf_qty, order.orderProductModel.qty.toString())
            binding.tvOrderConfPrice.text = order.orderProductModel.price.toCurrencyFormat()
            binding.tvOrderConfTotalPrice.text = order.orderProductModel.totalPrice.toCurrencyFormat()
            binding.tvOrderConfUnit.text = order.orderProductModel.unit

        }
    }

}