package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ProductWithUnits
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    //    val itemList = mutableListOf<MutableMap<String, String>>()
//    val itemList = mutableListOf<OrderHolder>()
    val itemList = mutableListOf<CartHolder>()

    fun readProductByIdWithUnits(productId: String): LiveData<ProductWithUnits> {
        return repository.readProductByIdWithUnits(productId)
    }

}