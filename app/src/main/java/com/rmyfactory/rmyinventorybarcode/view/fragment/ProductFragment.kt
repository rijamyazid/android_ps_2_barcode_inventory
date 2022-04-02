package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentProductBinding
import com.rmyfactory.rmyinventorybarcode.view.adapter.ProductAdapter
import com.rmyfactory.rmyinventorybarcode.viewmodel.ItemViewModel
import com.rmyfactory.rmyinventorybarcode.viewmodel.ProductCartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : BaseFragment() {

    private lateinit var binding: FragmentProductBinding
    private val viewModel: ItemViewModel by viewModels()
    private val productCartViewModel: ProductCartViewModel by activityViewModels()

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVars()

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        viewModel.readItemWithUnits().observe(viewLifecycleOwner, {
            productAdapter.addItems(it)
        })

        if(productCartViewModel.productCartState.value != 0) {
            binding.fabProductAdd.visibility = View.GONE
        } else {
            binding.fabProductAdd.visibility = View.VISIBLE
        }
        binding.fabProductAdd.setOnClickListener {
            findNavController().navigate(
                ProductFragmentDirections.actionItemFragmentToDetailActivity(System.currentTimeMillis().toString())
            )
        }

    }

    private fun initVars() {
        productAdapter = ProductAdapter {
            if(productCartViewModel.productCartState.value == 0) {
                findNavController().navigate(
                    ProductFragmentDirections.actionItemFragmentToDetailActivity(
                        it.item.itemId
                    )
                )
            } else {
                productCartViewModel.setProductCartState(2)
                productCartViewModel.itemWithUnits = it
                findNavController().popBackStack()
            }
        }
    }

}