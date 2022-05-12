package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderProduct2Binding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ProductUnitWithUnit
import com.rmyfactory.rmyinventorybarcode.util.toCurrencyFormat

class ProductAdapter2 : RecyclerView.Adapter<ProductAdapter2.ItemViewHolder>() {

    private val itemList = mutableListOf<ProductUnitWithUnit>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemHolderProduct2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    fun addProducts(products: List<ProductUnitWithUnit>) {
        itemList.clear()
        itemList.addAll(products)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(
        private val binding: ItemHolderProduct2Binding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductUnitWithUnit) {
            binding.tvProduct2Stock.text = product.productUnit.stock.toString()
            binding.tvProduct2Unit.text = product.productUnit.unitId
            binding.tvProduct2Price.text = product.productUnit.price.toCurrencyFormat()
        }

    }
}