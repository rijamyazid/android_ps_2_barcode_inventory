package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderProductBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits

class ProductAdapter(private val onclick: (itemId: String) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ItemViewHolder>() {

    private val itemList = mutableListOf<ItemWithUnits>()
    private lateinit var adapter2: ProductAdapter2

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
            adapter2 = ProductAdapter2()

            binding.tvItemIdItem.text = item.item.itemId
            binding.tvItemNameItem.text = item.item.itemName
            binding.rvProduct2.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = adapter2
            }
            Log.d("RMYFACTORYX", item.itemUnitList.toString())
            adapter2.addProducts(item.itemUnitList)

            binding.root.setOnClickListener {
                onclick(item.item.itemId)
            }
        }

    }

}