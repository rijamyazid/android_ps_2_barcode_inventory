package com.rmyfactory.rmyinventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithOrders
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(items: List<ItemModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: ItemModel)

    @Update
    fun updateItem(item: ItemModel)

    @Query("SELECT * FROM item_table WHERE id=:itemId")
    fun readItem(itemId: String): LiveData<ItemModel>

    @Query("SELECT * FROM item_table")
    fun readItems(): LiveData<List<ItemModel>>

    @Query("SELECT * FROM item_table")
    suspend fun _readItems(): List<ItemModel>

    @Query("DELETE FROM item_table WHERE id=:itemId")
    fun deleteItemById(itemId: String)

    @Transaction
    @Query("SELECT * FROM item_table")
    fun readItemsWithOrders(): LiveData<List<ItemWithOrders>>

    @Transaction
    @Query("SELECT * FROM item_table")
    fun readItemWithUnits(): LiveData<List<ItemWithUnits>>

    @Transaction
    @Query("SELECT * FROM item_table")
    suspend fun readItemWithUnits_(): List<ItemWithUnits>

    @Transaction
    @Query("SELECT * FROM item_table WHERE id=:itemId")
    fun readItemByIdWithUnits(itemId: String): LiveData<ItemWithUnits>

}