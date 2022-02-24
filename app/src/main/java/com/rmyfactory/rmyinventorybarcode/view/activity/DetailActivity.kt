package com.rmyfactory.rmyinventorybarcode.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.rmyfactory.rmyinventorybarcode.databinding.ActivityDetailBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.ItemModel
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
            viewModel.insertItem(
                ItemModel(itemId = args.itemId)
            )
        }

    }
}