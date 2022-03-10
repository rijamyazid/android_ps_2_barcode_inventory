package com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "item_unit_table", primaryKeys = ["item_id", "unit_id"])
data class ItemUnitModel(

    @ColumnInfo(name = "item_id", index = true)
    val itemId: String,
    @ColumnInfo(name = "unit_id", index = true)
    val unitId: String,
    @ColumnInfo(name = "stock")
    val stock: Int,
    @ColumnInfo(name = "price")
    val price: String
)