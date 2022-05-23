package com.rmyfactory.inventorybarcode.view.adapter_paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.databinding.ItemHolderLogBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat
import java.text.SimpleDateFormat
import java.util.*

class LogPagingAdapter (private val onItemClick: (orderId: OrderModel) -> Unit) :
    PagingDataAdapter<OrderModel, LogPagingAdapter.ViewHolder>(OrderComparator) {

    object OrderComparator : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel) =
            oldItem.orderId == newItem.orderId

        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHolderLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemModel = getItem(position)
        holder.bind(itemModel)
    }

    inner class ViewHolder(
        private val binding: ItemHolderLogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: OrderModel?) {
            log?.let {
                val sdf = SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale("id", "ID"))
                val dateFormatted = sdf.format(log.orderDate!!)
                binding.tvDate.text = dateFormatted
                binding.tvId.text = log.orderId
                binding.tvTotalPrice.text = log.orderTotalPrice.toCurrencyFormat()

                binding.root.setOnClickListener {
                    onItemClick(log)
                }
            }
        }

    }
}