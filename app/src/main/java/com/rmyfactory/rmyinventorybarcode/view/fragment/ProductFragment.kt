package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentProductBinding
import com.rmyfactory.rmyinventorybarcode.view.adapter.ProductAdapter
import com.rmyfactory.rmyinventorybarcode.viewmodel.ProductCartViewModel
import com.rmyfactory.rmyinventorybarcode.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : BaseFragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private val productCartViewModel: ProductCartViewModel by activityViewModels()

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVars()

        binding.svProduct.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.updateProductQuery(newText.toString())
                return true
            }

        })

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        viewModel.readProductWithUnits().observe(viewLifecycleOwner, {
            productAdapter.addProducts(it)
        })

        viewModel.productWithUnitsByQuery.observe(viewLifecycleOwner, {
            productAdapter.addProducts(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initVars() {
        productAdapter = ProductAdapter {
            if(productCartViewModel.productCartState.value == 0) {
                findNavController().navigate(
                    ProductFragmentDirections.actionItemFragmentToDetailActivity(
                        it.product.productId
                    )
                )
            } else {
                productCartViewModel.setProductCartState(2)
                productCartViewModel.productWithUnits = it
                findNavController().popBackStack()
            }
        }
    }

}