package com.rmyfactory.rmyinventorybarcode.model.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "item_table", primaryKeys = ["item_id"])
data class ItemModel(

    @ColumnInfo(name = "item_id")
    val itemId: String,
    @ColumnInfo(name = "item_name")
    var itemName: String,
    @ColumnInfo(name = "item_price")
    var itemPrice: String,
    @ColumnInfo(name = "item_stock")
    var itemStock: Int
)
