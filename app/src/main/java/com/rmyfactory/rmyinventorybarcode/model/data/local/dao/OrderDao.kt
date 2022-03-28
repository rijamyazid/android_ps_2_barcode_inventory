package com.rmyfactory.rmyinventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.OrderWithItems

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrder(order: OrderModel)

    @Query("SELECT * FROM order_table")
    suspend fun _readOrders(): List<OrderModel>

    @Query("SELECT * FROM order_table WHERE id=:orderId")
    fun readOrderById(orderId: String): LiveData<OrderModel>

    @Transaction
    @Query("SELECT * FROM order_table")
    fun readOrderWithItems(): LiveData<List<OrderWithItems>>

}