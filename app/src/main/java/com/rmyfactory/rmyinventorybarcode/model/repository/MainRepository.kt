package com.rmyfactory.rmyinventorybarcode.model.repository

import androidx.lifecycle.LiveData
import com.rmyfactory.rmyinventorybarcode.model.data.local.LocalDataSource
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.OrderWithItems
import javax.inject.Inject

class MainRepository
@Inject constructor(private val localDataSource: LocalDataSource) {

    fun insertItem(item: ItemModel) {
        localDataSource.insertItem(item)
    }

    fun insertOrder(order: OrderModel) {
        localDataSource.insertOrder(order)
    }

    fun insertOrderItems(orders: List<OrderItemModel>) {
        localDataSource.insertOrderItems(orders)
    }

    fun readItems(): LiveData<List<ItemModel>> {
        return localDataSource.readItems()
    }

    fun readItemById(itemId: String): LiveData<ItemModel> {
        return localDataSource.readItemById(itemId)
    }

    fun readOrderWithItems(): LiveData<List<OrderWithItems>> {
        return localDataSource.readOrderWithItems()
    }

    fun deleteItemById(itemId: String) {
        localDataSource.deleteItemById(itemId)
    }

    fun updateItem(item: ItemModel) {
        localDataSource.updateItem(item)
    }

}