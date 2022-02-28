package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderHolder
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    //    val itemList = mutableListOf<MutableMap<String, String>>()
    val itemList = mutableListOf<OrderHolder>()

    fun readItemById(itemId: String): LiveData<ItemModel> {
        return repository.readItemById(itemId)
    }

}