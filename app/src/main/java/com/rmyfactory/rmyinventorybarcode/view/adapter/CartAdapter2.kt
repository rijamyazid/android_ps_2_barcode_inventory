package com.rmyfactory.rmyinventorybarcode.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmyfactory.rmyinventorybarcode.databinding.ItemHolderCart2Binding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartUnitHolder
import com.rmyfactory.rmyinventorybarcode.util.toCurrencyFormat

class CartAdapter2(private val unitPos: (Int, Boolean) -> Unit) :
    RecyclerView.Adapter<CartAdapter2.CartViewHolder>() {

    private val itemUnitWithUnit = mutableListOf<CartUnitHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            ItemHolderCart2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val itemUnitWithUnit = itemUnitWithUnit[position]
        holder.bind(itemUnitWithUnit, position)
    }

    override fun getItemCount() = itemUnitWithUnit.size

    fun addItemUnits(itemUnitWithUnit: List<CartUnitHolder>) {
        this.itemUnitWithUnit.clear()
        this.itemUnitWithUnit.addAll(itemUnitWithUnit)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: ItemHolderCart2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartUnit: CartUnitHolder, position: Int) {

            binding.tvCartPrice.text = cartUnit.productPrice.toCurrencyFormat()
            binding.tvCartStock.text = cartUnit.productStock.toString()
            binding.tvCartUnit.text = cartUnit.productUnit
            binding.tvCartQty.text = cartUnit.productQty.toString()

            binding.imgIncreaseQty.setOnClickListener {
                unitPos(position, true)
                binding.tvCartQty.text =
                    (binding.tvCartQty.text.toString().toInt() + 1).toString()
            }

            binding.imgDecreaseQty.setOnClickListener {
                if (binding.tvCartQty.text.toString().toInt() > 0) {
                    unitPos(position, false)
                    binding.tvCartQty.text =
                        (binding.tvCartQty.text.toString().toInt() - 1).toString()
                }
            }
        }

    }
}