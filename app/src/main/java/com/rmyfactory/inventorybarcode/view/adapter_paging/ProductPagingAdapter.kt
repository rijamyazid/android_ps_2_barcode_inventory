package com.rmyfactory.inventorybarcode.view.adapter_paging

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.databinding.ItemHolderProductBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import com.rmyfactory.inventorybarcode.view.adapter.ProductAdapter2

class ProductPagingAdapter (private val onItemClick: (productId: ProductWithUnits) -> Unit) :
    PagingDataAdapter<ProductWithUnits, ProductPagingAdapter.ViewHolder>(ProductComparator) {

    private val itemList = mutableListOf<ProductWithUnits>()
    private lateinit var adapter2: ProductAdapter2

    object ProductComparator : DiffUtil.ItemCallback<ProductWithUnits>() {
        override fun areItemsTheSame(oldItem: ProductWithUnits, newItem: ProductWithUnits) =
            oldItem.product.productId == newItem.product.productId

        override fun areContentsTheSame(oldItem: ProductWithUnits, newItem: ProductWithUnits) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHolderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemModel = getItem(position)
        holder.bind(itemModel)
    }

    override fun getItemCount(): Int {

        Log.d("Productt", "Banyak data ${super.getItemCount()}")
        return super.getItemCount()
    }

    fun addProducts(products: List<ProductWithUnits>) {
        itemList.clear()
        itemList.addAll(products)
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemHolderProductBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductWithUnits?) {
            Log.d("RMYFACTORY1", product.toString())
            product?.let {
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
}