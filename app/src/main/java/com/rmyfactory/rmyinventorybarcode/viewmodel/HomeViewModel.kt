package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.BaseModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_ITEM
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_ITEM_UNIT
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_ORDER
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_ORDER_ITEM
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_UNIT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    fun importData(mapOfImportedData: Map<String, List<BaseModel>>) = viewModelScope.launch(Dispatchers.IO) {
        val listOfItems = mapOfImportedData[CONSTANT_TABLE_ITEM]
        val listOfOrders = mapOfImportedData[CONSTANT_TABLE_ORDER]
        val listOfUnits = mapOfImportedData[CONSTANT_TABLE_UNIT]
        val listOfOrderItems = mapOfImportedData[CONSTANT_TABLE_ORDER_ITEM]
        val listOfItemUnits = mapOfImportedData[CONSTANT_TABLE_ITEM_UNIT]

        listOfItems?.let {
            repository.insertItems(it.filterIsInstance<ItemModel>())
        }

        listOfOrders?.let {
            repository.insertOrders(it.filterIsInstance<OrderModel>())
        }

        listOfUnits?.let {
            repository.insertUnits(it.filterIsInstance<UnitModel>())
        }

        listOfOrderItems?.let {
            repository.insertOrderItems(it.filterIsInstance<OrderItemModel>())
        }

        listOfItemUnits?.let {
            repository.insertItemUnits(it.filterIsInstance<ItemUnitModel>())
        }

    }
}