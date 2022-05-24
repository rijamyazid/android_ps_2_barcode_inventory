package com.rmyfactory.inventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.inventorybarcode.databinding.FragmentLogDetailBinding
import com.rmyfactory.inventorybarcode.util.responses
import com.rmyfactory.inventorybarcode.util.toCurrencyFormat
import com.rmyfactory.inventorybarcode.view.adapter.LogAdapter2
import com.rmyfactory.inventorybarcode.viewmodel.LogDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class LogDetailFragment : Fragment() {

    private var _binding: FragmentLogDetailBinding? = null
    private val binding get() = _binding!!
    private val args: LogDetailFragmentArgs by navArgs()
    private val viewModel: LogDetailViewModel by viewModels()

    private lateinit var logAdapter2: LogAdapter2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setOrderDetailId(args.orderId)
        logAdapter2 = LogAdapter2(requireContext())
        binding.rvLog.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = logAdapter2
        }

        binding.btnBackTb.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.orderDetailLiveData.observe(viewLifecycleOwner, { response ->
            response.responses(
                isSuccess = {
                    val sdf = SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale("id", "ID"))
                    val dateFormatted = sdf.format(it.order.orderDate!!)
                    binding.tvDate.text = dateFormatted
                    binding.tvId.text = it.order.orderId

                    logAdapter2.submitData(it.orderWithProducts)

                    binding.tvTotalExchange.text = it.order.orderExchange.toCurrencyFormat()
                    binding.tvTotalPay.text = it.order.orderPay.toCurrencyFormat()
                    binding.tvTotalPrice.text = it.order.orderTotalPrice.toCurrencyFormat()

                    binding.pbLogDetail.visibility = View.GONE
                    binding.llContentLogDetail.visibility = View.VISIBLE
                },
                isFailure = {
                    binding.pbLogDetail.visibility = View.GONE
                }
            )
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}