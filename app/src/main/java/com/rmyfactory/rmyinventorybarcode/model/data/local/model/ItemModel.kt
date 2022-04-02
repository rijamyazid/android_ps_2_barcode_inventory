package com.rmyfactory.rmyinventorybarcode.model.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class ItemModel(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val itemId: String,
    @ColumnInfo(name = "item_name")
    var itemName: String,
    @ColumnInfo(name = "item_note")
    var itemNote: String = ""

): BaseModel
