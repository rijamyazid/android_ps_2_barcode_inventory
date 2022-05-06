package com.rmyfactory.rmyinventorybarcode.model.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.*
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ProductUnitModel

@Database(
    entities =
    [
        OrderModel::class,
        OrderProductModel::class,
        ProductModel::class,
        UnitModel::class,
        ProductUnitModel::class
    ],
    version = 1, exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun orderProductDao(): OrderProductDao
    abstract fun unitDao(): UnitDao
    abstract fun productUnitDao(): ProductUnitDao

}