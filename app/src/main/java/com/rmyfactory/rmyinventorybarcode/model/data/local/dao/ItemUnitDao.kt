package com.rmyfactory.rmyinventorybarcode.model.data.local.dao

import androidx.room.*
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel

@Dao
interface ItemUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemUnit(itemUnit: ItemUnitModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemUnits(itemUnits: List<ItemUnitModel>)

    @Update
    fun updateItemUnits(itemUnits: List<ItemUnitModel>)

    @Query("DELETE FROM item_unit_table WHERE item_id=:itemId")
    fun deleteItemUnitsByItemId(itemId: String)

    @Query("SELECT * FROM item_unit_table WHERE item_id=:itemId AND unit_id=:unitId")
    fun readItemByItemAndUnitId(itemId: String, unitId: String): ItemUnitModel?

}