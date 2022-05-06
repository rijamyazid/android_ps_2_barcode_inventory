package com.rmyfactory.rmyinventorybarcode.model.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class ProductModel(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val productId: String,
    @ColumnInfo(name = "product_name")
    var productName: String,
    @ColumnInfo(name = "product_note")
    var productNote: String = ""

): BaseModel
