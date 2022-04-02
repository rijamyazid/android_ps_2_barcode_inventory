package com.rmyfactory.rmyinventorybarcode.model.data.local

import androidx.lifecycle.LiveData
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.*
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.OrderWithItems
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.UnitWithItems
import javax.inject.Inject

class LocalDataSource
@Inject constructor(
    private val itemDao: ItemDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val unitDao: UnitDao,
    private val itemUnitDao: ItemUnitDao
) {

    //Item Model
    fun insertItem(item: ItemModel) {
        itemDao.insertItem(item)
    }

    fun insertItems(items: List<ItemModel>) {
        itemDao.insertItems(items)
    }

    fun updateItem(item: ItemModel) {
        itemDao.updateItem(item)
    }

    fun deleteItemById(itemId: String) {
        itemDao.deleteItemById(itemId)
    }

    fun readItems()
            : LiveData<List<ItemModel>> = itemDao.readItems()

    suspend fun _readItems(): List<ItemModel> = itemDao._readItems()

    fun readItemById(itemId: String)
            : LiveData<ItemModel> = itemDao.readItem(itemId)

    fun readItemWithUnits()
            : LiveData<List<ItemWithUnits>> = itemDao.readItemWithUnits()

    suspend fun readItemWithUnits_(): List<ItemWithUnits> = itemDao.readItemWithUnits_()

    fun readItemByIdWithUnits(itemId: String)
            : LiveData<ItemWithUnits> = itemDao.readItemByIdWithUnits(itemId)

    //Order Model
    fun insertOrder(order: OrderModel) {
        orderDao.insertOrder(order)
    }

    fun insertOrders(orders: List<OrderModel>) {
        orderDao.insertOrders(orders)
    }

    fun readOrderWithItems()
            : LiveData<List<OrderWithItems>> = orderDao.readOrderWithItems()

    suspend fun _readOrders(): List<OrderModel> = orderDao._readOrders()

    //Unit Model
    fun insertUnit(unit: UnitModel) {
        unitDao.insertUnit(unit)
    }

    fun insertUnits(unitList: List<UnitModel>) {
        unitDao.insertUnits(unitList)
    }

    fun updateUnits(unitList: List<UnitModel>) {
        unitDao.updateUnits(unitList)
    }

    fun readUnits()
            : LiveData<List<UnitModel>> = unitDao.readUnits()

    suspend fun _readUnits(): List<UnitModel> = unitDao._readUnits()

    fun readUnitById(unitId: String)
            : UnitModel? = unitDao.readUnitById(unitId)

    fun readUnitByNameWithItems(unitId: String)
            : LiveData<UnitWithItems> = unitDao.readUnitByIdWithItems(unitId = unitId)

    //OrderItem Model
    fun insertOrderItems(orderList: List<OrderItemModel>) {
        orderItemDao.insertOrderItems(orderList)
    }

    suspend fun _readOrderItems(): List<OrderItemModel> = orderItemDao._readOrderItems()

    //ItemUnit Model
    fun insertItemUnit(itemUnit: ItemUnitModel) {
        itemUnitDao.insertItemUnit(itemUnit)
    }

    fun insertItemUnits(itemUnitList: List<ItemUnitModel>) {
        itemUnitDao.insertItemUnits(itemUnitList)
    }

    fun updateItemUnits(itemUnitList: List<ItemUnitModel>) {
        itemUnitDao.updateItemUnits(itemUnitList)
    }

    fun deleteItemUnitById(id: Long) {
        itemUnitDao.deleteItemUnitById(id)
    }

    fun deleteItemUnitsByItemId(itemId: String) {
        itemUnitDao.deleteItemUnitsByItemId(itemId)
    }

    suspend fun _readItemUnits(): List<ItemUnitModel> = itemUnitDao._readItemUnits()

    fun readItemByItemAndUnitId(itemId: String, unitId: String): ItemUnitModel?
    = itemUnitDao.readItemByItemAndUnitId(itemId, unitId)

}