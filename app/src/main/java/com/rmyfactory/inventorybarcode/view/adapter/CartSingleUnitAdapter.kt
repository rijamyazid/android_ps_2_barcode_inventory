package com.rmyfactory.inventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.ItemHolderCartSingleUnitBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder2
import com.rmyfactory.inventorybarcode.util.logger
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat

class CartSingleUnitAdapter(private val onQtyChangeListener: (Int, Boolean) -> Unit) :
    RecyclerView.Adapter<CartSingleUnitAdapter.ViewHolder>() {

    private val products = mutableListOf<CartHolder2>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHolderCartSingleUnitBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        logger(msg = "position = $position, list = $products")
        val product = products[position]
        holder.rootView().animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_in_translate)
        holder.bind(product, position)
    }

    override fun getItemCount() = products.size

    fun submitToCart(products: List<CartHolder2>) {
        this.products.clear()
        this.products.addAll(products)
//        notifyItemInserted(0)
        notifyDataSetChanged()
    }

    fun removeFromCartPos(pos: Int) {
        this.products.removeAt(pos)
//        notifyItemRemoved(pos)
        notifyDataSetChanged()
    }

    fun clearCart() {
        this.products.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemHolderCartSingleUnitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun rootView() = binding.root

        fun bind(product: CartHolder2, position: Int) {
            binding.tvCartProductId.text = product.productId
            binding.tvCartProductName.text = product.productName
            binding.tvCartProductQty.text = product.productQty.toString()

            binding.imgIncreaseQty.setOnClickListener {
                val currentQty: Int = binding.tvCartProductQty.text.toString().toInt()
                binding.tvCartProductQty.text = (currentQty + 1).toString()
                onQtyChangeListener(position, true)
            }
            binding.imgDecreaseQty.setOnClickListener {
                val currentQty: Int = binding.tvCartProductQty.text.toString().toInt()
                if (currentQty > 0) {
                    binding.tvCartProductQty.text = (currentQty - 1).toString()
                    onQtyChangeListener(position, false)
                }
            }

            binding.tvCartProductStock.text = product.productStock.toString()
            binding.tvCartProductUnit.text = product.productUnit
            binding.tvCartProductPrice.text = product.productPrice.toCurrencyFormat()
        }

    }

}