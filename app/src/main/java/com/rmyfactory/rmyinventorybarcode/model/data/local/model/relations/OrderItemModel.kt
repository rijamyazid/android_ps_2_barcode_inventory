package com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.BaseModel

@Entity(tableName = "order_item_table", primaryKeys = ["order_id", "item_id"])
data class OrderItemModel(

    @ColumnInfo(name = "order_id", index = true)
    var orderId: String,
    @ColumnInfo(name = "item_id", index = true)
    var itemId: String,
    @ColumnInfo(name = "quantity")
    val qty: Int,
    @ColumnInfo(name = "price")
    val price: String,
    @ColumnInfo(name = "total_price")
    val totalPrice: String
): BaseModel
