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

    fun updateItem(item: ItemModel) {
        localDataSource.updateItem(item)
    }

    fun deleteItemById(itemId: String) {
        localDataSource.deleteItemById(itemId)
    }

    fun readItems()
            : LiveData<List<ItemModel>> = localDataSource.readItems()

    fun readItemById(itemId: String)
            : LiveData<ItemModel> = localDataSource.readItemById(itemId)

    fun readItemWithUnits()
            : LiveData<List<ItemWithUnits>> = localDataSource.readItemWithUnits()

    fun readItemByIdWithUnits(itemId: String)
            : LiveData<ItemWithUnits> = localDataSource.readItemByIdWithUnits(itemId)

    //Order Model
    fun insertOrder(order: OrderModel) {
        localDataSource.insertOrder(order)
    }

    fun readOrderWithItems()
            : LiveData<List<OrderWithItems>> = localDataSource.readOrderWithItems()

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
    :LiveData<List<UnitModel>> = localDataSource.readUnits()

    fun readUnitById(unitId: String)
            : UnitModel? = localDataSource.readUnitById(unitId)

    fun readUnitByNameWithItems(unitName: String)
            : LiveData<UnitWithItems> = localDataSource.readUnitByNameWithItems(unitName)

    //OrderItem Model
    fun insertOrderItems(orders: List<OrderItemModel>) {
        localDataSource.insertOrderItems(orders)
    }

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

    fun deleteItemUnitsByItemId(itemId: String) {
        localDataSource.deleteItemUnitsByItemId(itemId)
    }

}