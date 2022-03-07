package com.rmyfactory.rmyinventorybarcode.view.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentOrderConfirmationBinding
import com.rmyfactory.rmyinventorybarcode.util.Functions
import com.rmyfactory.rmyinventorybarcode.view.adapter.OrderConfAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: FragmentOrderConfirmationBinding
    private val args: OrderConfirmationActivityArgs by navArgs()
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

        orderConfAdapter.addOrders(args.orders.toList())
        var sumPrice = 0L
        args.orders.forEach {
            sumPrice += (it.itemPrice.toLong() * it.itemQty)
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
}