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
import com.rmyfactory.inventorybarcode.databinding.FragmentProductDetailBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.inventorybarcode.util.Functions
import com.rmyfactory.inventorybarcode.util.ifEmptySetDefault
import com.rmyfactory.inventorybarcode.util.responses
import com.rmyfactory.inventorybarcode.view.adapter.DetailUnitAdapter
import com.rmyfactory.inventorybarcode.viewmodel.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailFragmentArgs by navArgs()
    private val viewModel: ProductDetailViewModel by viewModels()
    private lateinit var detailUnitAdapter: DetailUnitAdapter
    private lateinit var productDetail: ProductDetailHolder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productDetail = Functions.fillProductDetailHolder()

        viewModel.readProductWithUnitsById(args.itemId)

        detailUnitAdapter = DetailUnitAdapter(requireContext(),
            onUnitRemoved = { isAdd, position, id ->
                if (isAdd) {
                    viewModel.productUnitRemovedIds[position] = id
                } else {
                    viewModel.productUnitRemovedIds.remove(position)
                }
            })

        binding.rvDetailUnit.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = detailUnitAdapter
        }

        binding.edtItemId.editText?.setText(args.itemId)

        binding.btnBackTb.setOnClickListener(AppBarOnClickListener())

        binding.btnUnit2.setOnClickListener(MainOnClickListener())
        binding.btnAddItem.setOnClickListener(MainOnClickListener())
        binding.btnUpdateItem.setOnClickListener(MainOnClickListener())
        binding.btnDeleteItem.setOnClickListener(MainOnClickListener())

        viewModel.productWithUnitsResult.observe(this, { response ->
            response.responses(
                isSuccess = {
                    binding.edtItemName.editText?.setText(it.product.productName)
                    binding.edtItemNote.editText?.setText(it.product.productNote)
                    if (it.productUnitList.isNotEmpty()) {
                        detailUnitAdapter.addItemUnits(
                            it.productUnitList.size,
                            it.productUnitList
                        )
                    }
                    binding.btnAddItem.visibility = View.GONE
                    binding.llButtonsDetail.visibility = View.VISIBLE

                    binding.pbProductDetail.visibility = View.GONE
                    binding.llContent.visibility = View.VISIBLE
                },
                isFailure = {
                    binding.btnAddItem.visibility = View.VISIBLE
                    binding.llButtonsDetail.visibility = View.GONE

                    binding.pbProductDetail.visibility = View.GONE
                    binding.llContent.visibility = View.VISIBLE
                }
            )
        })

        viewModel.readUnits().observe(this, { unitModels ->
            val units = mutableListOf("Pilih...")
            unitModels.forEach {
                units.add(it.unitId)
            }
            units.add("Tambah...")
            detailUnitAdapter.addSpinnerList(units)
        })
    }

    inner class AppBarOnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.btnBackTb.id -> {
                    findNavController().popBackStack()
                }
            }
        }
    }

    inner class MainOnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.btnAddItem.id -> {
                    productDetail.productUnit.clear()
                    productDetail.productPrice.clear()
                    productDetail.productStock.clear()

                    val edtItemName = binding.edtItemName.editText?.text.toString()
                    val edtItemNote = binding.edtItemNote.editText?.text.toString()

                    productDetail.productId = args.itemId
                    productDetail.productName = edtItemName.ifEmptySetDefault("No Name")
                    productDetail.productNote = edtItemNote.ifEmptySetDefault("")

                    detailUnitAdapter.getBindingList().forEach {

                        if (it.value.spinItemUnitRv.selectedItem.toString() != "Pilih..." ||
                            it.value.edtItemPriceRv.editText?.text.toString().isNotEmpty() ||
                            it.value.edtItemStockRv.editText?.text.toString().isNotEmpty()
                        ) {

                            if (it.value.contSpinnerRv.visibility == View.VISIBLE) {
                                if (it.value.spinItemUnitRv.selectedItem.toString() == "Pilih...") {
                                    productDetail.productUnit.add("Buah")
                                } else {
                                    productDetail.productUnit.add(
                                        it.value.spinItemUnitRv.selectedItem.toString()
                                    )
                                }
                            } else {
                                productDetail.productUnit.add(
                                    it.value.edtItemUnitRv.editText?.text.toString()
                                        .ifEmptySetDefault("Buah")
                                )
                            }
                            productDetail.productPrice.add(
                                it.value.edtItemPriceRv.editText?.text.toString()
                                    .ifEmptySetDefault("0")
                            )
                            productDetail.productStock.add(
                                it.value.edtItemStockRv.editText?.text.toString()
                                    .ifEmptySetDefault("0")
                                    .toInt()
                            )
                            productDetail.productIncrement.add(
                                it.value.edtItemIncrementRv.editText?.text.toString()
                                    .ifEmptySetDefault("1.0")
                                    .toFloat()
                            )
                        }
                    }

                    viewModel.insertProduct(
                        productDetail
                    )

                    findNavController().popBackStack()
                }

                binding.btnUpdateItem.id -> {
                    productDetail.productUnit.clear()
                    productDetail.productPrice.clear()
                    productDetail.productStock.clear()

                    val edtItemName = binding.edtItemName.editText?.text.toString()
                    val edtItemNote = binding.edtItemNote.editText?.text.toString()

                    productDetail.productId = args.itemId
                    productDetail.productName = edtItemName.ifEmptySetDefault("No Name")
                    productDetail.productNote = edtItemNote.ifEmptySetDefault("")

                    detailUnitAdapter.getBindingList().forEach {

                        if (it.value.contSpinnerRv.visibility == View.VISIBLE) {
                            if (it.value.spinItemUnitRv.selectedItem.toString() == "Pilih...") {
                                productDetail.productUnit.add("Buah")
                            } else {
                                productDetail.productUnit.add(
                                    it.value.spinItemUnitRv.selectedItem.toString()
                                )
                            }
                        } else {
                            productDetail.productUnit.add(
                                it.value.edtItemUnitRv.editText?.text.toString()
                                    .ifEmptySetDefault("Buah")
                            )
                        }
                        productDetail.productPrice.add(
                            it.value.edtItemPriceRv.editText?.text.toString().ifEmptySetDefault("0")
                        )
                        productDetail.productStock.add(
                            it.value.edtItemStockRv.editText?.text.toString().ifEmptySetDefault("0")
                                .toInt()
                        )
                        productDetail.productIncrement.add(
                            it.value.edtItemIncrementRv.editText?.text.toString()
                                .ifEmptySetDefault("1.0")
                                .toFloat()
                        )
                    }

                    viewModel.updateProduct(
                        productDetail
                    )

                    findNavController().popBackStack()
                }

                binding.btnDeleteItem.id -> {
                    viewModel.deleteProduct(args.itemId)
                    findNavController().popBackStack()
                }

                binding.btnUnit2.id -> {
                    detailUnitAdapter.addItemUnits((detailUnitAdapter.getUnitListSize() + 1))
                }
            }
        }

    }
}