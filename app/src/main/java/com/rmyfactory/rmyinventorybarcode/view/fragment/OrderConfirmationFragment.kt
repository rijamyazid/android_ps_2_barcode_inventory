package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentOrderConfirmationBinding

class OrderConfirmationFragment : Fragment() {

    private lateinit var binding: FragmentOrderConfirmationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderConfirmationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}