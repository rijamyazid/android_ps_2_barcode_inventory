package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderProductBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits
import com.rmyfactory.rmyinventorybarcode.util.Functions.dotPriceIND

class ProductAdapter(private val onclick: (itemId: String) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ItemViewHolder>() {

    private val itemList = mutableListOf<ItemWithUnits>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemHolderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    fun addItems(items: List<ItemWithUnits>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(
        private val binding: ItemHolderProductBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemWithUnits) {
            binding.tvItemIdItem.text = item.item.itemId
            binding.tvItemNameItem.text = item.item.itemName
            binding.tvItemStockItem.text = item.itemUnitList[0].itemUnit.stock.toString()
            binding.tvItemPriceItem.text = dotPriceIND(item.itemUnitList[0].itemUnit.price)

            binding.root.setOnClickListener {
                onclick(item.item.itemId)
            }
        }

    }

}