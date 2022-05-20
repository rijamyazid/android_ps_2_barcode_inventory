package com.rmyfactory.inventorybarcode.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.databinding.ItemHolderProductBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits

class ProductAdapter(private val onItemClick: (productId: ProductWithUnits) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ItemViewHolder>() {

    private val itemList = mutableListOf<ProductWithUnits>()
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

    fun addProducts(products: List<ProductWithUnits>) {
        itemList.clear()
        itemList.addAll(products)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(
        private val binding: ItemHolderProductBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductWithUnits) {
            adapter2 = ProductAdapter2()

            binding.tvItemIdItem.text = product.product.productId
            binding.tvItemNameItem.text = product.product.productName
            binding.rvProduct2.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = adapter2
            }
            Log.d("RMYFACTORYX", product.productUnitList.toString())
            adapter2.addProducts(product.productUnitList)

            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }

    }

}