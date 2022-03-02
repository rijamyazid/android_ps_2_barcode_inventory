package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentOrderConfirmationBinding
import com.rmyfactory.rmyinventorybarcode.util.Functions.dotPriceIND
import com.rmyfactory.rmyinventorybarcode.view.adapter.OrderConfAdapter

class OrderConfirmationFragment : Fragment() {

    private lateinit var binding: FragmentOrderConfirmationBinding
    private val args: OrderConfirmationFragmentArgs by navArgs()
    private lateinit var orderConfAdapter: OrderConfAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        var sumPrice = 0L
        args.orderItems.forEach {
            sumPrice += (it.itemPrice.toLong() * it.itemQty)
        }
        binding.tvOrderConfSummPrice.text = dotPriceIND(sumPrice.toString())

        binding.edtOrderConfPay.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvOrderConfExchange.text = dotPriceIND("0")
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