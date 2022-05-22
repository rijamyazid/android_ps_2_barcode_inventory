package com.rmyfactory.inventorybarcode.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rmyfactory.inventorybarcode.databinding.ActivityLogDetailBinding

class LogDetailActivity : AppCompatActivity() {

    private var _binding: ActivityLogDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}