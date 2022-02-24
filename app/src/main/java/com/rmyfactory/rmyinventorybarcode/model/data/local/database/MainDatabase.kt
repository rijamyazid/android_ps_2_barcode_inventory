package com.rmyfactory.rmyinventorybarcode.model.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rmyfactory.rmyinventorybarcode.model.data.local.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.ItemDao

@Database(entities = [ItemModel::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

}