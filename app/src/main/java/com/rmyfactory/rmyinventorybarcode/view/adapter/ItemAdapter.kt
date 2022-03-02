package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderItemBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.util.Functions.dotPriceIND

class ItemAdapter(private val onclick: (itemId: String) -> Unit) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val itemList = mutableListOf<ItemModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemHolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    fun addItems(items: List<ItemModel>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(
        private val binding: ItemHolderItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemModel) {
            binding.tvItemIdItem.text = item.itemId
            binding.tvItemNameItem.text = item.itemName
            binding.tvItemStockItem.text = item.itemStock.toString()
            binding.tvItemPriceItem.text = dotPriceIND(item.itemPrice)

            binding.root.setOnClickListener {
                onclick(item.itemId)
            }
        }

    }

}