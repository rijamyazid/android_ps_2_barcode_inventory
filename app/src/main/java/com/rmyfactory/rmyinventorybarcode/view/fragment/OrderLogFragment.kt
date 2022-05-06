package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentOrderLogBinding
import com.rmyfactory.rmyinventorybarcode.viewmodel.OrderLogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderLogFragment : BaseFragment() {

    private lateinit var binding: FragmentOrderLogBinding
    private val viewModel: OrderLogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.readOrderWithItems().observe(viewLifecycleOwner, {
            var text = ""
            it.forEach { o ->
                text += "Order: ${o.order.orderId}\n"
                o.orderWithProducts.forEach { oi ->
                    text += "Item name: ${oi.product.productName}\n" +
                            "Item qty: ${oi.orderProductModel.qty}\n\n"
                }
                text += "\n"
            }
            binding.text.text = text
        })

    }

}