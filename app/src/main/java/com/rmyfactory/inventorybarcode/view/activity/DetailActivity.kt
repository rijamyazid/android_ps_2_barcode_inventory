package com.rmyfactory.inventorybarcode.view.activity

import android.os.Bundle
import android.util.Log
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

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var detailUnitAdapter: DetailUnitAdapter
    private lateinit var productDetail: ProductDetailHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productDetail = fillProductDetailHolder()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.readProductWithUnitsById(args.itemId)

        detailUnitAdapter = DetailUnitAdapter(this,
            onUnitRemoved = { isAdd, position, id ->
                Log.d("RMYFACTORYX", "isAdd? $isAdd")
                if (isAdd) {
//                    detailUnitAdapter.refreshSize(detailUnitAdapter.getUnitListSize() - 1)
                    viewModel.productUnitRemovedIds[position] = id
                } else {
                    viewModel.productUnitRemovedIds.remove(position)
//                    detailUnitAdapter.refreshSize(detailUnitAdapter.getUnitListSize() + 1)
//                    viewModel.itemUnitRemovedIds.forEachIndexed breaking@ { index, value ->
//                        if (value[1] == id) {
//                            viewModel.itemUnitRemovedIds.removeAt(index)
//                            return@breaking
//                        }
//                    }
                }
//                viewModel.deleteItemUnitById(it)
            })

        binding.rvDetailUnit.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = detailUnitAdapter
        }

        binding.edtItemId.editText?.setText(args.itemId)

        binding.btnUnit2.setOnClickListener {
            detailUnitAdapter.addItemUnits((detailUnitAdapter.getUnitListSize() + 1))
        }

        binding.btnAddItem.setOnClickListener {

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
            }

            viewModel.insertProduct(
                productDetail
            )

            finish()
        }

        binding.btnUpdateItem.setOnClickListener {

            productDetail.productUnit.clear()
            productDetail.productPrice.clear()
            productDetail.productStock.clear()

            val edtItemName = binding.edtItemName.editText?.text.toString()
            val edtItemNote = binding.edtItemNote.editText?.text.toString()

            productDetail.productId = args.itemId
            productDetail.productName = edtItemName.ifEmptySetDefault("No Name")
            productDetail.productNote = edtItemNote.ifEmptySetDefault("")

            detailUnitAdapter.getBindingList().forEach {

//                if(it.value.spinItemUnitRv.selectedItem.toString() != "Pilih..." ||
//                    it.value.edtItemPriceRv.editText?.text.toString().isNotEmpty() ||
//                    it.value.edtItemStockRv.editText?.text.toString().isNotEmpty()) {

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
                        it.value.edtItemUnitRv.editText?.text.toString().ifEmptySetDefault("Buah")
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
                    it.value.edtItemIncrementRv.editText?.text.toString().ifEmptySetDefault("1.0")
                        .toFloat()
                )
//                }
            }

//            detailUnitAdapter.addItemUnits((detailUnitAdapter.getUnitListSize() - viewModel.itemUnitRemovedIds.size))
            viewModel.updateProduct(
                productDetail
            )

            finish()
        }

        binding.btnDeleteItem.setOnClickListener {
            viewModel.deleteProduct(args.itemId)
            finish()

//            detailUnitAdapter.getBindingList().forEach {
//                Log.d("RMYFACTORYX", "binding size = ${detailUnitAdapter.getBindingList().size}\n" +
//                        "Cek Price At Binding ${it.key} = ${it.value.edtItemPriceRv.editText?.text.toString()}")
//            }
        }
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

//    override fun onBackPressed() {
////        val initialUnit = detailUnitAdapter.getUnitListSize()
////        if ((initialUnit - viewModel.itemUnitRemovedIds.size) < initialUnit) {
////            detailUnitAdapter.addItemUnits((detailUnitAdapter.getUnitListSize() + viewModel.itemUnitRemovedIds.size))
////        }
//        super.onBackPressed()
//        viewModel.itemUnitRemovedIds.clear()
//    }
}