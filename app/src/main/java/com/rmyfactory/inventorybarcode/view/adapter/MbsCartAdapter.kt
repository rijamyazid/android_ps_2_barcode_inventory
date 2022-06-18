package com.rmyfactory.inventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.databinding.ItemHolderMbsCartBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductUnitWithUnit
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits

class MbsCartAdapter(private val onItemClick: (ProductUnitWithUnit) -> Unit = {}) : RecyclerView.Adapter<MbsCartAdapter.ViewHolder>() {

    private var productWithUnits: ProductWithUnits? = null
    private val unitProducts = mutableListOf<ProductUnitWithUnit>()
    private val productsOnCart = mutableMapOf<String, Map<String, Boolean>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHolderMbsCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = unitProducts[position]
        holder.bind(product)
    }

    override fun getItemCount() = unitProducts.size

    fun submitToCartMbs(productWithUnits: ProductWithUnits, productsOnCart: Map<String, Map<String, Boolean>>) {
        this.productWithUnits = productWithUnits

        this.unitProducts.clear()
        this.unitProducts.addAll(productWithUnits.productUnitList)

        this.productsOnCart.clear()
        this.productsOnCart.putAll(productsOnCart)

        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemHolderMbsCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(unitProduct: ProductUnitWithUnit) {
            binding.tvIhCartUnit.text = unitProduct.unit.unitId

            productWithUnits?.let {
                if(productsOnCart[it.product.productId] != null) {
                    if(productsOnCart[it.product.productId]?.get(unitProduct.unit.unitId) != null) {
                        binding.root.isClickable = false
                        binding.root.isEnabled = false
                    }
                }
            }

            binding.root.setOnClickListener {
                binding.root.isClickable = false
                binding.root.isEnabled = false
                onItemClick(unitProduct)
            }

        }

    }

}