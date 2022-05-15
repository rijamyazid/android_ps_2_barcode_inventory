package com.rmyfactory.inventorybarcode.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.inventorybarcode.databinding.FragmentOrderBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartUnitHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.inventorybarcode.util.Functions.millisToOrderId
import com.rmyfactory.inventorybarcode.util.responses
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat
import com.rmyfactory.inventorybarcode.view.adapter.OrderAdapter
import com.rmyfactory.inventorybarcode.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class OrderFragment : BaseFragment() {

    private lateinit var binding: FragmentOrderBinding
    private val viewModel: OrderViewModel by viewModels()
    private val args: OrderFragmentArgs by navArgs()
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var order: OrderModel
    private lateinit var orderProducts: MutableList<OrderProductModel>
    private var sumPrice by Delegates.notNull<Long>()
    private var exchange by Delegates.notNull<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderAdapter = OrderAdapter(requireContext())
        binding.rvOrderConf.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }

        Log.d("RMYDFACTORYX", "OrderFrag1: ${args.products.toList()}")
        val productWithoutZeroQty = args.products.toList().filterZeroQtyProduct()
        Log.d("RMYDFACTORYX", "OrderFrag2: $productWithoutZeroQty")
        orderAdapter.addProducts(productWithoutZeroQty)
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
                        unit = cartUnit.productUnit,
                        qty = cartUnit.productQty,
                        price = cartUnit.productPrice,
                        totalPrice = totalPrice.toString()
                    )
                )
                Log.d("RMYDFACTORYX", "OrderFrag3: $cartUnit")
            }
        }
        binding.tvOrderConfSummPrice.text = sumPrice.toString().toCurrencyFormat()

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
            viewModel.insertOrders(
                order,
                orderProducts
            )
        }

        binding.edtOrderConfPay.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvOrderConfExchange.text = "Rp. 0"
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    exchange = (p0.toString().toLong() - sumPrice)
                    if (exchange < 0) {
                        binding.tvOrderConfExchange.setTextColor(Color.RED)
                    } else {
                        binding.tvOrderConfExchange.setTextColor(Color.GRAY)
                    }
                    binding.tvOrderConfExchange.text = exchange.toString().toCurrencyFormat()

                } else {
                    binding.tvOrderConfExchange.setTextColor(Color.GRAY)
                    binding.tvOrderConfExchange.text = "Rp. 0"
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        )

        viewModel.productWithUnitsResult.observe(viewLifecycleOwner, { data ->
            data.responses(
                isSuccess = {
                    findNavController().popBackStack()
                })
        })

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