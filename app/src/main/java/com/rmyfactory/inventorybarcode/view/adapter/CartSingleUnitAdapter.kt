package com.rmyfactory.inventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.databinding.ItemHolderCartSingleUnitBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder2
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat

class CartSingleUnitAdapter :
    RecyclerView.Adapter<CartSingleUnitAdapter.ViewHolder>() {

    private val products = mutableListOf<CartHolder2>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHolderCartSingleUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount() = products.size

    fun submitToCart(products: List<CartHolder2>) {
        this.products.clear()
        this.products.addAll(products)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemHolderCartSingleUnitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: CartHolder2) {
            binding.tvCartProductId.text = product.productId
            binding.tvCartProductName.text = product.productName
            binding.tvCartProductQty.text = product.productQty.toString()
            binding.tvCartProductStock.text = product.productStock.toString()
            binding.tvCartProductUnit.text = product.productUnit
            binding.tvCartProductPrice.text = product.productPrice.toCurrencyFormat()
        }

    }

}