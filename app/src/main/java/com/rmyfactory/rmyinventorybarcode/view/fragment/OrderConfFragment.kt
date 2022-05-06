package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentOrderConfirmationBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartUnitHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.rmyinventorybarcode.util.Functions.dotPriceIND
import com.rmyfactory.rmyinventorybarcode.util.Functions.millisToOrderId
import com.rmyfactory.rmyinventorybarcode.view.adapter.OrderConfAdapter
import com.rmyfactory.rmyinventorybarcode.viewmodel.OrderConfViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class OrderConfFragment : BaseFragment() {

    private lateinit var binding: FragmentOrderConfirmationBinding
    private val confViewModel: OrderConfViewModel by viewModels()
    private val args: OrderConfFragmentArgs by navArgs()
    private lateinit var orderConfAdapter: OrderConfAdapter
    private lateinit var order: OrderModel
    private lateinit var orderProducts: MutableList<OrderProductModel>
    private var sumPrice by Delegates.notNull<Long>()
    private var exchange by Delegates.notNull<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderConfirmationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderConfAdapter = OrderConfAdapter(requireContext())
        binding.rvOrderConf.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderConfAdapter
        }

        Log.d("RMYDFACTORYX", "OrderFrag1: ${args.products.toList()}")
        val productWithoutZeroQty = args.products.toList().filterZeroQtyProduct()
        Log.d("RMYDFACTORYX", "OrderFrag2: $productWithoutZeroQty")
        orderConfAdapter.addProducts(productWithoutZeroQty)
        sumPrice = 0L
        orderProducts = mutableListOf()
        productWithoutZeroQty.forEach { cart ->
            cart.productUnits.forEach { cartUnit ->
                val totalPrice = (cartUnit.productPrice.toLong() * cartUnit.productQty)
                sumPrice += totalPrice
                orderProducts.add(
                    OrderProductModel(
                        orderId = "0",
                        productId = cart.productId,
                        qty = cartUnit.productQty,
                        price = cartUnit.productPrice,
                        totalPrice = totalPrice.toString()
                    )
                )
            }
        }
        binding.tvOrderConfSummPrice.text = dotPriceIND(sumPrice.toString())

        binding.btnConfirmOrder.setOnClickListener {
            val orderTime = millisToOrderId(System.currentTimeMillis())
            order = OrderModel(
                orderId = orderTime,
                orderTotalPrice = sumPrice.toString(),
                orderPay = binding.edtOrderConfPay.editText?.text.toString(),
                orderExchange = exchange.toString()
            )
            orderProducts.forEach {
                it.orderId = orderTime
            }
            confViewModel.insertOrders(
                order,
                orderProducts
            )
        }

        binding.edtOrderConfPay.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvOrderConfExchange.text = dotPriceIND("0")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    exchange = (p0.toString().toLong() - sumPrice)
                    if (exchange < 0) {
                        binding.tvOrderConfExchange.setTextColor(Color.RED)
                    } else {
                        binding.tvOrderConfExchange.setTextColor(Color.GRAY)
                    }
                    binding.tvOrderConfExchange.text =
                        dotPriceIND(exchange.toString())

                } else {
                    binding.tvOrderConfExchange.setTextColor(Color.GRAY)
                    binding.tvOrderConfExchange.text =
                        dotPriceIND("0")
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
                if (cartUnit.productQty > 0) {
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
            if (productUnitWithoutZeroQty.isNotEmpty()) {
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