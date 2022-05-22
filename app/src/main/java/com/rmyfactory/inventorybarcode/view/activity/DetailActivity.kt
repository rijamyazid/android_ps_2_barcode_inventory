package com.rmyfactory.inventorybarcode.view.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.inventorybarcode.databinding.ActivityDetailBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.inventorybarcode.util.Functions.fillProductDetailHolder
import com.rmyfactory.inventorybarcode.util.ResponseResult
import com.rmyfactory.inventorybarcode.util.ifEmptySetDefault
import com.rmyfactory.inventorybarcode.view.adapter.DetailUnitAdapter
import com.rmyfactory.inventorybarcode.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailActivityArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var detailUnitAdapter: DetailUnitAdapter
    private lateinit var productDetail: ProductDetailHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productDetail = fillProductDetailHolder()

        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.readProductWithUnitsById(args.itemId)

        detailUnitAdapter = DetailUnitAdapter(this,
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

        viewModel.productWithUnitsResult.observe(this, {
            when (it) {
                is ResponseResult.Loading -> {
                }
                is ResponseResult.Success -> {
                    binding.edtItemName.editText?.setText(it.data.product.productName)
                    binding.edtItemNote.editText?.setText(it.data.product.productNote)
                    if (it.data.productUnitList.isNotEmpty()) {
                        detailUnitAdapter.addItemUnits(
                            it.data.productUnitList.size,
                            it.data.productUnitList
                        )
                    }
                    binding.btnAddItem.visibility = View.GONE
                    binding.llButtonsDetail.visibility = View.VISIBLE
                }
                is ResponseResult.Failure -> {
                    binding.btnAddItem.visibility = View.VISIBLE
                    binding.llButtonsDetail.visibility = View.GONE
                }
                else -> {
                }
            }
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
                    finish()
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

                    finish()
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

                    finish()
                }

                binding.btnDeleteItem.id -> {
                    viewModel.deleteProduct(args.itemId)
                    finish()
                }

                binding.btnUnit2.id -> {
                    detailUnitAdapter.addItemUnits((detailUnitAdapter.getUnitListSize() + 1))
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}