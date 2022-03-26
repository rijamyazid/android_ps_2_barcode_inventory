package com.rmyfactory.rmyinventorybarcode.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmyfactory.rmyinventorybarcode.databinding.ActivityDetailBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.rmyinventorybarcode.util.Functions.fillProductDetailHolder
import com.rmyfactory.rmyinventorybarcode.util.ifEmptySetDefault
import com.rmyfactory.rmyinventorybarcode.view.adapter.DetailUnitAdapter
import com.rmyfactory.rmyinventorybarcode.viewmodel.DetailViewModel
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

        detailUnitAdapter = DetailUnitAdapter(this,
            onUnitRemoved = { isAdd, position, id ->
                Log.d("RMYFACTORYX", "isAdd? $isAdd")
                if (isAdd) {
//                    detailUnitAdapter.refreshSize(detailUnitAdapter.getUnitListSize() - 1)
                    viewModel.itemUnitRemovedIds.add(listOf(position.toLong(), id))
                } else {
//                    detailUnitAdapter.refreshSize(detailUnitAdapter.getUnitListSize() + 1)
                    viewModel.itemUnitRemovedIds.forEachIndexed { index, value ->
                        if (value[1] == id) {
                            viewModel.itemUnitRemovedIds.removeAt(index)
                        }
                    }
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

                    productDetail.productUnit.add(
                        it.value.spinItemUnitRv.selectedItem.toString()
                    )
                    productDetail.productPrice.add(
                        it.value.edtItemPriceRv.editText?.text.toString().ifEmptySetDefault("0")
                    )
                    productDetail.productStock.add(
                        it.value.edtItemStockRv.editText?.text.toString().ifEmptySetDefault("0")
                            .toInt()
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

                productDetail.productUnit.add(
                    it.value.spinItemUnitRv.selectedItem.toString()
                )
                productDetail.productPrice.add(
                    it.value.edtItemPriceRv.editText?.text.toString().ifEmptySetDefault("0")
                )
                productDetail.productStock.add(
                    it.value.edtItemStockRv.editText?.text.toString().ifEmptySetDefault("0")
                        .toInt()
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

        viewModel.readItemByIdWithUnits(args.itemId).observe(this, {
            if (viewModel.firstInit) {
                if (it != null) {

//                if(it.itemUnitList.size > 1) {
//                    binding.btnUnit2.visibility = View.GONE
//                    binding.constraint2.visibility = View.VISIBLE
//                    binding.btnUnitDelete2.visibility = View.VISIBLE
//                }

                    binding.edtItemName.editText?.setText(it.item.itemName)
                    binding.edtItemNote.editText?.setText(it.item.itemNote)
                    if (it.itemUnitList.isNotEmpty()) {
                        detailUnitAdapter.addItemUnits(it.itemUnitList.size, it.itemUnitList)
                    }
                    binding.btnAddItem.visibility = View.GONE
                    binding.llButtonsDetail.visibility = View.VISIBLE
                } else {
                    binding.btnAddItem.visibility = View.VISIBLE
                    binding.llButtonsDetail.visibility = View.GONE
                }
                viewModel.firstInit = false
            }
        })

        viewModel.readUnits().observe(this, { unitModels ->
            val units = mutableListOf("Pilih...")
            unitModels.forEach {
                units.add(it.unitId)
            }
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