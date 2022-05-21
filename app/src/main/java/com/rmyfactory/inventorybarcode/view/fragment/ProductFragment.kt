package com.rmyfactory.inventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.inventorybarcode.databinding.FragmentProductBinding
import com.rmyfactory.inventorybarcode.view.activity.MainActivity
import com.rmyfactory.inventorybarcode.view.adapter.ProductAdapter
import com.rmyfactory.inventorybarcode.view.adapter_paging.ProductPagingAdapter
import com.rmyfactory.inventorybarcode.viewmodel.MainActivityViewModel
import com.rmyfactory.inventorybarcode.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : BaseFragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productPagingAdapter: ProductPagingAdapter

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

        binding.svProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
            adapter = productPagingAdapter
        }

        viewModel.readProductWithUnits().observe(viewLifecycleOwner, {
            lifecycleScope.launchWhenCreated {
                productPagingAdapter.submitData(it)
            }
        })

        viewModel.productWithUnitsByQuery.observe(viewLifecycleOwner, {
            lifecycleScope.launchWhenCreated {
                productPagingAdapter.submitData(it)
            }
        })

        if (mainActivityViewModel.productCartState != 0) {
            binding.fabProductAdd.visibility = View.GONE
            (activity as MainActivity).hideBottomNav()
        } else {
            binding.fabProductAdd.visibility = View.VISIBLE
        }

        binding.fabProductAdd.setOnClickListener {
            findNavController().navigate(
                ProductFragmentDirections.actionItemFragmentToDetailActivity(
                    System.currentTimeMillis().toString()
                )
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainActivityViewModel.productCartState = 2
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initVars() {
        productPagingAdapter = ProductPagingAdapter {
            if (mainActivityViewModel.productCartState == 0) {
                findNavController().navigate(
                    ProductFragmentDirections.actionItemFragmentToDetailActivity(
                        it.product.productId
                    )
                )
            } else {
                mainActivityViewModel.productCartState = 2
                mainActivityViewModel.productWithUnits.value = it
                findNavController().popBackStack()
            }
        }
        productAdapter = ProductAdapter {
            if (mainActivityViewModel.productCartState == 0) {
                findNavController().navigate(
                    ProductFragmentDirections.actionItemFragmentToDetailActivity(
                        it.product.productId
                    )
                )
            } else {
                mainActivityViewModel.productCartState = 2
                mainActivityViewModel.productWithUnits.value = it
                findNavController().popBackStack()
            }
        }
    }
}