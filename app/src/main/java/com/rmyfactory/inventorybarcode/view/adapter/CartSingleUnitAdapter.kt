package com.rmyfactory.inventorybarcode.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.ItemHolderCartSingleUnitBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder2
import com.rmyfactory.inventorybarcode.util.logger
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat

@SuppressLint("NotifyDataSetChanged")
class CartSingleUnitAdapter(private val onQtyChangeListener: (Int, Boolean) -> Unit) :
    RecyclerView.Adapter<CartSingleUnitAdapter.ViewHolder>() {

    companion object {
        const val ANIME_TYPE_NONE = "ANIME_TYPE_NONE"
        const val ANIME_TYPE_INSERT = "ANIME_TYPE_INSERT"
    }

    private var animeType = ANIME_TYPE_NONE
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
        if (position == 0 && animeType == ANIME_TYPE_INSERT) {
            holder.rootView().animation =
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_in_translate)
        }
        holder.bind(product, position)
    }

    override fun getItemCount() = products.size

    fun submitToCart(products: List<CartHolder2>, animeType: String) {
        this.products.clear()
        this.products.addAll(products)
        this.animeType = animeType
        notifyDataSetChanged()
    }

    fun refreshCart(products: List<CartHolder2>) {
        this.products.clear()
        this.products.addAll(products)
        notifyDataSetChanged()
    }

    fun removeFromCartPos(pos: Int) {
        this.products.removeAt(pos)
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
                binding.tvCartProductQty.text =
                    binding.root.context.resources.getString(R.string.value_number, currentQty + 1)
                onQtyChangeListener(position, true)
            }
            binding.imgDecreaseQty.setOnClickListener {
                val currentQty: Int = binding.tvCartProductQty.text.toString().toInt()
                if (currentQty > 0) {
                    binding.tvCartProductQty.text = binding.root.context.resources.getString(
                        R.string.value_number,
                        currentQty - 1
                    )
                    onQtyChangeListener(position, false)
                }
            }

            binding.tvCartProductStock.text = product.productStock.toString()
            binding.tvCartProductUnit.text = product.productUnit
            binding.tvCartProductPrice.text = product.productPrice.toCurrencyFormat()
        }

    }

}