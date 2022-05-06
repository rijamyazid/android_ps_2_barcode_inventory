package com.rmyfactory.rmyinventorybarcode.model.repository

import androidx.lifecycle.LiveData
import com.rmyfactory.rmyinventorybarcode.model.data.local.LocalDataSource
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.OrderWithItems
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.UnitWithItems
import javax.inject.Inject

class MainRepository
@Inject constructor(private val localDataSource: LocalDataSource) {

    //Item Model
    fun insertItem(item: ItemModel) {
        localDataSource.insertItem(item)
    }

    fun insertItems(items: List<ItemModel>) {
        localDataSource.insertItems(items)
    }

    fun updateItem(item: ItemModel) {
        localDataSource.updateItem(item)
    }

    fun deleteItemById(itemId: String) {
        localDataSource.deleteItemById(itemId)
    }

    fun readItems()
            : LiveData<List<ItemModel>> = localDataSource.readItems()

    suspend fun _readItems(): List<ItemModel> = localDataSource._readItems()

    fun readItemById(itemId: String)
            : LiveData<ItemModel> = localDataSource.readItemById(itemId)

    fun readItemWithUnits()
            : LiveData<List<ItemWithUnits>> = localDataSource.readItemWithUnits()

    suspend fun readItemWithUnits_(): List<ItemWithUnits> = localDataSource.readItemWithUnits_()

    fun readItemByIdWithUnits(itemId: String)
            : LiveData<ItemWithUnits> = localDataSource.readItemByIdWithUnits(itemId)

    //Order Model
    fun insertOrder(order: OrderModel) {
        localDataSource.insertOrder(order)
    }

    fun insertOrders(orders: List<OrderModel>) {
        localDataSource.insertOrders(orders)
    }

    fun readOrderWithItems()
            : LiveData<List<OrderWithItems>> = localDataSource.readOrderWithItems()

    suspend fun _readOrders(): List<OrderModel> = localDataSource._readOrders()

    //Unit Model
    fun insertUnit(unit: UnitModel) {
        localDataSource.insertUnit(unit)
    }

    fun insertUnits(unitList: List<UnitModel>) {
        localDataSource.insertUnits(unitList)
    }

    fun updateUnits(unitList: List<UnitModel>) {
        localDataSource.updateUnits(unitList)
    }

    fun readUnits()
            : LiveData<List<UnitModel>> = localDataSource.readUnits()

    fun readItemWithUnitsByQuery(query: String): LiveData<List<ItemWithUnits>>
    = localDataSource.readItemWithUnitsByQuery(query)

    suspend fun susReadItemWithUnitsByQuery(query: String)
            : List<ItemWithUnits> = localDataSource.susReadItemWithUnitsByQuery(query)

    suspend fun _readUnits(): List<UnitModel> = localDataSource._readUnits()

    fun readUnitById(unitId: String)
            : UnitModel? = localDataSource.readUnitById(unitId)

    fun readUnitByNameWithItems(unitName: String)
            : LiveData<UnitWithItems> = localDataSource.readUnitByNameWithItems(unitName)

    //OrderItem Model
    fun insertOrderItems(orders: List<OrderItemModel>) {
        localDataSource.insertOrderItems(orders)
    }

    suspend fun _readOrderItems(): List<OrderItemModel> = localDataSource._readOrderItems()

    //ItemUnitModel
    fun readItemByItemAndUnitId(itemId: String, unitId: String)
            : ItemUnitModel? = localDataSource.readItemByItemAndUnitId(itemId, unitId)

    fun insertItemUnit(itemUnit: ItemUnitModel) {
        localDataSource.insertItemUnit(itemUnit)
    }

    fun insertItemUnits(itemUnitList: List<ItemUnitModel>) {
        localDataSource.insertItemUnits(itemUnitList)
    }

    fun updateItemUnits(itemUnitList: List<ItemUnitModel>) {
        localDataSource.updateItemUnits(itemUnitList)
    }

    fun deleteItemUnitById(id: Long) {
        localDataSource.deleteItemUnitById(id)
    }

    fun deleteItemUnitsByItemId(itemId: String) {
        localDataSource.deleteItemUnitsByItemId(itemId)
    }

    suspend fun _readItemUnits(): List<ItemUnitModel> = localDataSource._readItemUnits()

}