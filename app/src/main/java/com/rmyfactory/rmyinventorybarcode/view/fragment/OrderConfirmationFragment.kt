package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentOrderConfirmationBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.util.Functions.dotPriceIND
import com.rmyfactory.rmyinventorybarcode.util.Functions.millisToOrderId
import com.rmyfactory.rmyinventorybarcode.view.adapter.OrderConfAdapter
import com.rmyfactory.rmyinventorybarcode.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class OrderConfirmationFragment : BaseFragment() {

    private lateinit var binding: FragmentOrderConfirmationBinding
    private val viewModel: OrderViewModel by viewModels()
    private val args: OrderConfirmationFragmentArgs by navArgs()
    private lateinit var orderConfAdapter: OrderConfAdapter
    private lateinit var order: OrderModel
    private lateinit var orderItems: MutableList<OrderItemModel>
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

        orderConfAdapter.addOrders(args.orderItems.toList())
        sumPrice = 0L
        orderItems = mutableListOf()
        args.orderItems.forEach {
            val totalPrice = (it.itemPrice.toLong() * it.itemQty)
            sumPrice += totalPrice
            orderItems.add(
                OrderItemModel(
                    orderId = "0",
                    itemId = it.itemId,
                    qty = it.itemQty,
                    price = it.itemPrice,
                    totalPrice = totalPrice.toString()
                )
            )
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
            orderItems.forEach {
                it.orderId = orderTime
            }
            viewModel.insertOrders(
                order,
                orderItems
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

}