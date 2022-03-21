package com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_unit_table")
data class ItemUnitModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "item_id", index = true)
    val itemId: String,
    @ColumnInfo(name = "unit_id", index = true)
    val unitId: String,
    @ColumnInfo(name = "stock")
    val stock: Int,
    @ColumnInfo(name = "price")
    val price: String
)