package com.rmyfactory.rmyinventorybarcode.view.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.rmyfactory.rmyinventorybarcode.databinding.ActivityDetailBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.rmyinventorybarcode.util.Functions.fillProductDetailHolder
import com.rmyfactory.rmyinventorybarcode.util.ifEmptySetDefault
import com.rmyfactory.rmyinventorybarcode.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var productDetail: ProductDetailHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productDetail = fillProductDetailHolder()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtItemId.editText?.setText(args.itemId)

        binding.btnAddItem.setOnClickListener {
            val edtItemName = binding.edtItemName.editText?.text.toString()
            val edtItemPrice = binding.edtItemPrice.editText?.text.toString()
            val edtItemStock = binding.edtItemStock.editText?.text.toString()

            productDetail.productId = args.itemId
            productDetail.productName = edtItemName.ifEmptySetDefault("No Name")
            productDetail.productUnit = listOf("Barang")
            productDetail.productPrice = listOf(edtItemPrice.ifEmptySetDefault("0"))
            productDetail.productStock = listOf(edtItemStock.ifEmptySetDefault("0").toInt())

            viewModel.insertProduct(
                productDetail
            )

            finish()
        }

        binding.btnUpdateItem.setOnClickListener {
            val edtItemName = binding.edtItemName.editText?.text.toString()
            val edtItemPrice = binding.edtItemPrice.editText?.text.toString()
            val edtItemStock = binding.edtItemStock.editText?.text.toString()

            productDetail.productId = args.itemId
            productDetail.productName = edtItemName.ifEmptySetDefault("No Name")
            productDetail.productUnit = listOf("Barang")
            productDetail.productPrice = listOf(edtItemPrice.ifEmptySetDefault("0"))
            productDetail.productStock = listOf(edtItemStock.ifEmptySetDefault("0").toInt())

            viewModel.updateProduct(
                productDetail
            )

            finish()
        }

        binding.btnDeleteItem.setOnClickListener {
            viewModel.deleteProduct(args.itemId)
            finish()
        }

        viewModel.readItemByIdWithUnits(args.itemId).observe(this, {
            if (it != null) {
                binding.edtItemName.editText?.setText(it.item.itemName)
                binding.edtItemPrice.editText?.setText(it.itemUnitList[0].itemUnit.price)
                binding.edtItemStock.editText?.setText(it.itemUnitList[0].itemUnit.stock.toString())

                binding.btnAddItem.visibility = View.GONE
                binding.llButtonsDetail.visibility = View.VISIBLE
            } else {
                binding.btnAddItem.visibility = View.VISIBLE
                binding.llButtonsDetail.visibility = View.GONE
            }
        })

        viewModel.readUnits().observe(this, {
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                it.map { unit -> unit.unitId }
            ).also { adapter -> binding.spinItemUnit.adapter = adapter }
        })

    }
}