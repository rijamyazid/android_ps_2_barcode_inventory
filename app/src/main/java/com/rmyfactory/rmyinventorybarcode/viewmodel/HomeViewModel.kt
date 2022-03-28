package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(private val repository: MainRepository): ViewModel() {

    suspend fun _readItems(): List<ItemModel> = repository._readItems()
    suspend fun _readOrders(): List<OrderModel> = repository._readOrders()
    suspend fun _readUnits(): List<UnitModel> = repository._readUnits()
    suspend fun _readItemUnits(): List<ItemUnitModel> = repository._readItemUnits()
    suspend fun _readOrderItems(): List<OrderItemModel> = repository._readOrderItems()
    fun readItems(): LiveData<List<ItemModel>> = repository.readItems()

}