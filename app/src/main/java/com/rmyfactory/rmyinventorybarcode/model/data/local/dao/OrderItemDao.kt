package com.rmyfactory.rmyinventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel

@Dao
interface OrderItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderItems(orderItems: List<OrderItemModel>)

    @Query("SELECT * FROM order_item_table WHERE order_id=:orderId")
    fun readOrderItemsByOrderId(orderId: String): LiveData<List<OrderItemModel>>

    @Query("SELECT * FROM order_item_table")
    fun readOrderItems(): LiveData<List<OrderItemModel>>

    @Query("SELECT * FROM order_item_table")
    suspend fun _readOrderItems(): List<OrderItemModel>

}