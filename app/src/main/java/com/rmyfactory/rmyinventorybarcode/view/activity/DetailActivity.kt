package com.rmyfactory.rmyinventorybarcode.view.activity

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.ActivityDetailBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.rmyinventorybarcode.util.CutoutDrawable
import com.rmyfactory.rmyinventorybarcode.util.Functions.fillProductDetailHolder
import com.rmyfactory.rmyinventorybarcode.util.ifEmptySetDefault
import com.rmyfactory.rmyinventorybarcode.util.themeColor
import com.rmyfactory.rmyinventorybarcode.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var productDetail: ProductDetailHolder
    private lateinit var spinnerAdapter: ArrayAdapter<String>

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

        viewModel.readUnits().observe(this, { unitModels ->

            val units = mutableListOf("Pilih...")
            unitModels.forEach {
                units.add(it.unitId)
            }

            spinnerAdapter = object: ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                units as List<String>
            )
            {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return super.getView(position, convertView, parent).apply {
                        setPadding(24, paddingTop, paddingEnd, paddingBottom)
                    }
                }
            }
            binding.spinItemUnit.adapter = spinnerAdapter

        })

        val borderUnit1Drawable = getCutoutDrawable(color = themeColor(R.attr.colorPrimary))
        binding.llUnit1.background = borderUnit1Drawable
        binding.tvBorderUnit1.addOnLayoutChangeListener {
                _, left, top, right, bottom, _, _, _, _ ->
            val realLeft = left - binding.llUnit1.left
            val realTop = top - binding.llUnit1.top
            val realRight = right - binding.llUnit1.left
            val realBottom = bottom - binding.llUnit1.top
            borderUnit1Drawable.setCutout(
                realLeft.toFloat(),
                realTop.toFloat(),
                realRight.toFloat(),
                realBottom.toFloat()
            )
        }

        val borderSpinner1Drawable = getCutoutDrawable(
            cornerSize = 8F, color = themeColor(R.attr.colorPrimary))
        binding.spinItemUnit.background = borderSpinner1Drawable
        binding.tvBorderSpinnerUnit1.addOnLayoutChangeListener {
                _, left, top, right, bottom, _, _, _, _ ->
            val realLeft = left - binding.spinItemUnit.left
            val realTop = top - binding.spinItemUnit.top
            val realRight = right - binding.spinItemUnit.left
            val realBottom = bottom - binding.spinItemUnit.top
            borderSpinner1Drawable.setCutout(
                realLeft.toFloat(),
                realTop.toFloat(),
                realRight.toFloat(),
                realBottom.toFloat()
            )
        }
    }

    private fun getRoundedCornerShape(cornerSize: Float) =
        ShapeAppearanceModel.Builder()
        .setAllCorners(RoundedCornerTreatment())
        .setAllCornerSizes(cornerSize)
        .build()

    private fun getCutoutDrawable(cornerSize: Float = 16F, strokeWidth: Float = 2f, color: Int) =
        CutoutDrawable(getRoundedCornerShape(cornerSize)).apply {
        setStroke(strokeWidth, color)
        fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
    }
}