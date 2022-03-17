package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderOrderConfirmationBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.OrderHolder
import com.rmyfactory.rmyinventorybarcode.util.dotPriceIND

class OrderConfAdapter(private val context: Context) :
    RecyclerView.Adapter<OrderConfAdapter.OrderConfViewHolder>() {

    private val productList = mutableListOf<OrderHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderConfViewHolder {
        val binding = ItemHolderOrderConfirmationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderConfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderConfViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount() = productList.size

    fun addProducts(products: List<CartHolder>) {
        productList.clear()
        productList.addAll(products.cartToListOfProduct())
        notifyDataSetChanged()
    }

    private fun List<CartHolder>.cartToListOfProduct(): List<OrderHolder> {

        val listOfProduct = mutableListOf<OrderHolder>()

        this.forEach { cart ->
            cart.productUnits.forEach { cartUnit ->
                listOfProduct.add(
                    OrderHolder(
                        productName = cart.productName,
                        productId = cart.productId,
                        productPrice = cartUnit.productPrice,
                        productUnit = cartUnit.productUnit,
                        productQty = cartUnit.productQty,
                    )
                )
            }
        }

        return listOfProduct

    }

    inner class OrderConfViewHolder(private val binding: ItemHolderOrderConfirmationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: OrderHolder) {
            binding.tvOrderConfId.text = product.productId
            binding.tvOrderConfName.text = product.productName
            binding.tvOrderConfPrice.text = product.productPrice.dotPriceIND()
            binding.tvOrderConfUnit.text = product.productUnit
            binding.tvOrderConfQty.text =
                context.getString(R.string.order_conf_qty, product.productQty.toString())
            binding.tvOrderConfTotalPrice.text =
                ((product.productPrice.toLong() * product.productQty).toString()).dotPriceIND()
        }

    }

}