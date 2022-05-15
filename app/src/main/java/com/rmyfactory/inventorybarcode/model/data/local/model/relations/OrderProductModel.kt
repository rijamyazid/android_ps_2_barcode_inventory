package com.rmyfactory.inventorybarcode.model.data.local.model.relations

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.rmyfactory.inventorybarcode.model.data.local.model.BaseModel

@Entity(tableName = "order_product_table", primaryKeys = ["order_id", "product_id", "unit"])
data class OrderProductModel(

    @ColumnInfo(name = "order_id", index = true)
    var orderId: String,
    @ColumnInfo(name = "product_id", index = true)
    var productId: String,
    @ColumnInfo(name = "unit")
    val unit: String,
    @ColumnInfo(name = "quantity")
    val qty: Int,
    @ColumnInfo(name = "price")
    val price: String,
    @ColumnInfo(name = "total_price")
    val totalPrice: String
): BaseModel
