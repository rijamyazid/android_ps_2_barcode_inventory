package com.rmyfactory.inventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.inventorybarcode.databinding.FragmentLogBinding
import com.rmyfactory.inventorybarcode.view.adapter.LogAdapter
import com.rmyfactory.inventorybarcode.view.adapter_paging.LogPagingAdapter
import com.rmyfactory.inventorybarcode.viewmodel.LogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogFragment : BaseFragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var logAdapter: LogAdapter
    private lateinit var logPagingAdapter: LogPagingAdapter
    private val viewModel: LogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logAdapter = LogAdapter()
        logPagingAdapter = LogPagingAdapter(onItemClick = {
            findNavController().navigate(LogFragmentDirections.actionLogFragmentToLogDetailFragment(it.orderId))
        })

        binding.rvLog.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = logPagingAdapter
        }

        viewModel.readOrders().observe(viewLifecycleOwner, {
            lifecycleScope.launchWhenStarted {
                logPagingAdapter.submitData(it)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}