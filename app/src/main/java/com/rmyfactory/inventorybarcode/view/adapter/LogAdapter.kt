package com.rmyfactory.inventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.databinding.ItemHolderLogBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.with.OrderWithProducts
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat
import java.text.SimpleDateFormat
import java.util.*

class LogAdapter: RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    private val orderProducts = mutableListOf<OrderWithProducts>()
    private lateinit var logAdapter2: LogAdapter2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHolderLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderProducts[position])
    }

    override fun getItemCount() = orderProducts.size

    fun addAdapterData(orderProducts: List<OrderWithProducts>) {
        this.orderProducts.clear()
        this.orderProducts.addAll(orderProducts)
        notifyItemRangeInserted(0, orderProducts.size)
    }

    inner class ViewHolder(private val binding: ItemHolderLogBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderWithProducts) {

            val sdf = SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale("id", "ID"))
            val dateFormatted = sdf.format(order.order.orderDate!!)
            binding.tvDate.text = dateFormatted
            binding.tvId.text = order.order.orderId

//            logAdapter2 = LogAdapter2(context)
//            binding.rvLog.apply {
//                layoutManager = LinearLayoutManager(context)
//                adapter = logAdapter2
//            }
//            logAdapter2.submitData(order.orderWithProducts)

            binding.tvTotalPrice.text = order.order.orderTotalPrice.toCurrencyFormat()
//            binding.tvTotalPay.text = order.order.orderPay.toCurrencyFormat()
//            binding.tvTotalExchange.text = order.order.orderExchange.toCurrencyFormat()
        }
    }

}