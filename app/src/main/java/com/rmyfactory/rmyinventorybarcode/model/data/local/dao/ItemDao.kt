package com.rmyfactory.rmyinventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rmyfactory.rmyinventorybarcode.model.data.local.ItemModel

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItems(items: List<ItemModel>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItem(item: ItemModel)

    @Update
    fun updateItem(item: ItemModel)

    @Query("SELECT * FROM item_table WHERE id=:itemId")
    fun readItem(itemId: String): LiveData<ItemModel>

    @Query("SELECT * FROM item_table")
    fun readItems(): LiveData<List<ItemModel>>

    @Query("DELETE FROM item_table WHERE id=:itemId")
    fun deleteItemById(itemId: String)

}