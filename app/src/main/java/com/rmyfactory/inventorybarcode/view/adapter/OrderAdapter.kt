package com.rmyfactory.inventorybarcode.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.ItemHolderOrderBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.OrderHolder
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat

class OrderAdapter(private val context: Context) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val productList = mutableListOf<OrderHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemHolderOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
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

    inner class OrderViewHolder(private val binding: ItemHolderOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: OrderHolder) {
            binding.tvOrderConfId.text = product.productId
            binding.tvOrderConfName.text = product.productName
            binding.tvOrderConfPrice.text = product.productPrice.toCurrencyFormat()
            binding.tvOrderConfUnit.text = product.productUnit
            binding.tvOrderConfQty.text =
                context.getString(R.string.order_conf_qty, product.productQty.toString())
            binding.tvOrderConfTotalPrice.text =
                ((product.productPrice.toLong() * product.productQty).toString()).toCurrencyFormat()
        }

    }

}