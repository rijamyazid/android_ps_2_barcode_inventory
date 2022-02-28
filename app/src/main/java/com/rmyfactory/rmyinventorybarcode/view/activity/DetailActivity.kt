package com.rmyfactory.rmyinventorybarcode.view.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.rmyfactory.rmyinventorybarcode.databinding.ActivityDetailBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtItemId.editText?.setText(args.itemId)

        binding.btnAddItem.setOnClickListener {
            val edtItemName = binding.edtItemName.editText?.text.toString()
            val edtItemPrice = binding.edtItemPrice.editText?.text.toString()
            val edtItemStock = binding.edtItemStock.editText?.text.toString()

            val itemName = if (edtItemName.isEmpty()) "No Name" else edtItemName
            val itemPrice = if (edtItemPrice.isEmpty()) "0" else edtItemPrice
            val itemStock = if (edtItemStock.isEmpty()) 0 else edtItemStock.toInt()

            viewModel.insertItem(
                ItemModel(
                    itemId = args.itemId,
                    itemName = itemName,
                    itemPrice = itemPrice,
                    itemStock = itemStock
                )
            )

            finish()
        }

        binding.btnUpdateItem.setOnClickListener {
            val edtItemName = binding.edtItemName.editText?.text.toString()
            val edtItemPrice = binding.edtItemPrice.editText?.text.toString()
            val edtItemStock = binding.edtItemStock.editText?.text.toString()

            val itemName = if (edtItemName.isEmpty()) "No Name" else edtItemName
            val itemPrice = if (edtItemPrice.isEmpty()) "0" else edtItemPrice
            val itemStock = if (edtItemStock.isEmpty()) 0 else edtItemStock.toInt()

            viewModel.updateItem(
                ItemModel(
                    itemId = args.itemId,
                    itemName = itemName,
                    itemPrice = itemPrice,
                    itemStock = itemStock
                )
            )

            finish()
        }

        binding.btnDeleteItem.setOnClickListener {
            viewModel.deleteItemById(args.itemId)
            finish()
        }

        viewModel.readItemById(args.itemId).observe(this, {
            if (it != null) {
                binding.edtItemName.editText?.setText(it.itemName)
                binding.edtItemPrice.editText?.setText(it.itemPrice)
                binding.edtItemStock.editText?.setText(it.itemStock.toString())

                binding.btnAddItem.visibility = View.GONE
                binding.llButtonsDetail.visibility = View.VISIBLE
            } else {
                binding.btnAddItem.visibility = View.VISIBLE
                binding.llButtonsDetail.visibility = View.GONE
            }
        })

    }
}