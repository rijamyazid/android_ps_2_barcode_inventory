package com.rmyfactory.rmyinventorybarcode.view.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentOrderConfirmationBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartUnitHolder
import com.rmyfactory.rmyinventorybarcode.util.Functions
import com.rmyfactory.rmyinventorybarcode.view.adapter.OrderConfAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConfActivity : AppCompatActivity() {

    private lateinit var binding: FragmentOrderConfirmationBinding
    private val args: OrderConfActivityArgs by navArgs()
    private lateinit var orderConfAdapter: OrderConfAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderConfAdapter = OrderConfAdapter(this)
        binding.rvOrderConf.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderConfAdapter
        }

        val productWithoutZeroQty = args.products.toList().filterZeroQtyProduct()
        orderConfAdapter.addProducts(productWithoutZeroQty)
        var sumPrice = 0L
        productWithoutZeroQty.forEach { cart ->
            cart.productUnits.forEach {
                sumPrice += (it.productPrice.toLong() * it.productQty)
            }
        }
        binding.tvOrderConfSummPrice.text = Functions.dotPriceIND(sumPrice.toString())

        binding.edtOrderConfPay.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvOrderConfExchange.text = Functions.dotPriceIND("0")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    val exchange = (p0.toString().toLong() - sumPrice)
                    if (exchange < 0) {
                        binding.tvOrderConfExchange.setTextColor(Color.RED)
                    } else {
                        binding.tvOrderConfExchange.setTextColor(Color.GRAY)
                    }
                    binding.tvOrderConfExchange.text =
                        Functions.dotPriceIND(exchange.toString())

                } else {
                    binding.tvOrderConfExchange.setTextColor(Color.GRAY)
                    binding.tvOrderConfExchange.text =
                        Functions.dotPriceIND("0")
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        )

    }

    private fun List<CartHolder>.filterZeroQtyProduct(): List<CartHolder> {
        val productWithoutZeroQty = mutableListOf<CartHolder>()
        this.forEach { cart ->
            val productUnitWithoutZeroQty = mutableListOf<CartUnitHolder>()
            cart.productUnits.forEach { cartUnit ->
                if(cartUnit.productQty > 0) {
                    productUnitWithoutZeroQty.add(
                        CartUnitHolder(
                            productPrice = cartUnit.productPrice,
                            productQty = cartUnit.productQty,
                            productUnit = cartUnit.productUnit,
                            productStock = cartUnit.productStock
                        )
                    )
                }
            }
            if(productUnitWithoutZeroQty.isNotEmpty()) {
                productWithoutZeroQty.add(
                    CartHolder(
                        productId = cart.productId,
                        productName = cart.productName,
                        productUnits = productUnitWithoutZeroQty
                    )
                )
            }
        }
        return productWithoutZeroQty
    }

}