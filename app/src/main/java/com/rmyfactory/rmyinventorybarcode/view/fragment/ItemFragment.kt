package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentItemBinding
import com.rmyfactory.rmyinventorybarcode.view.adapter.ItemAdapter
import com.rmyfactory.rmyinventorybarcode.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemFragment : Fragment() {

    private lateinit var binding: FragmentItemBinding
    private val viewModel: ItemViewModel by viewModels()

    private lateinit var itemAdapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentItemBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVars()

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }

        viewModel.readItems().observe(viewLifecycleOwner, {
            itemAdapter.addItems(it)
        })

    }

    private fun initVars() {
        itemAdapter = ItemAdapter {
            findNavController().navigate(
                ItemFragmentDirections.actionItemFragmentToDetailActivity(
                    it
                )
            )
        }
    }

}