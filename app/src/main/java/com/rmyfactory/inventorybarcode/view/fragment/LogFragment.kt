package com.rmyfactory.inventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.inventorybarcode.databinding.FragmentLogBinding
import com.rmyfactory.inventorybarcode.view.adapter.LogAdapter
import com.rmyfactory.inventorybarcode.viewmodel.LogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogFragment : BaseFragment() {

    private lateinit var binding: FragmentLogBinding
    private lateinit var logAdapter: LogAdapter
    private val viewModel: LogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logAdapter = LogAdapter()

        binding.rvLog.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = logAdapter
        }

        viewModel.readOrderWithItems().observe(viewLifecycleOwner, {
            logAdapter.addAdapterData(it)
        })

    }

}