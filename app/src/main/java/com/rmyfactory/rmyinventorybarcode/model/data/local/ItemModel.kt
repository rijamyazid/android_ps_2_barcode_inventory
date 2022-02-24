package com.rmyfactory.rmyinventorybarcode.model.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "item_table", primaryKeys = ["id"])
data class ItemModel(

    @ColumnInfo(name = "id")
    val itemId: String

)
