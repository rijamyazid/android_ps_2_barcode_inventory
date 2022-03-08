package com.rmyfactory.rmyinventorybarcode.model.data.local

import androidx.lifecycle.LiveData
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.ItemDao
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.OrderDao
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.OrderItemDao
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.OrderWithItems
import javax.inject.Inject

class LocalDataSource
@Inject constructor(
    private val itemDao: ItemDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {

    fun insertItem(item: ItemModel) {
        itemDao.insertItem(item)
    }

    fun insertOrder(order: OrderModel) {
        orderDao.insertOrder(order)
    }

    fun insertOrderItems(orders: List<OrderItemModel>) {
        orderItemDao.insertOrderItems(orders)
    }

    fun readItems(): LiveData<List<ItemModel>> {
        return itemDao.readItems()
    }

    fun readOrderWithItems(): LiveData<List<OrderWithItems>> {
        return orderDao.readOrderWithItems()
    }

    fun readItemById(itemId: String): LiveData<ItemModel> {
        return itemDao.readItem(itemId)
    }

    fun deleteItemById(itemId: String) {
        itemDao.deleteItemById(itemId)
    }

    fun updateItem(item: ItemModel) {
        itemDao.updateItem(item)
    }

}