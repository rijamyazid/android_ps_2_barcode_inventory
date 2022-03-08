package com.rmyfactory.rmyinventorybarcode.model.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.ItemDao
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.OrderDao
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.OrderItemDao
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel

@Database(
    entities = [OrderModel::class, OrderItemModel::class, ItemModel::class],
    version = 1, exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

}