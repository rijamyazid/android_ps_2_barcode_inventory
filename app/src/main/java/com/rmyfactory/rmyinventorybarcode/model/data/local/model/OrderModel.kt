package com.rmyfactory.rmyinventorybarcode.model.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "order_table", primaryKeys = ["order_id"])
data class OrderModel(

    @ColumnInfo(name = "order_id")
    val orderId: String,

    )
