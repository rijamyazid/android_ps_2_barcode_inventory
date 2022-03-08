package com.rmyfactory.rmyinventorybarcode.model.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_table")
data class OrderModel(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val orderId: String,
    @ColumnInfo(name = "order_total_price")
    val orderTotalPrice: String,
    @ColumnInfo(name = "order_pay")
    val orderPay: String,
    @ColumnInfo(name = "order_exchange")
    val orderExchange: String

)
