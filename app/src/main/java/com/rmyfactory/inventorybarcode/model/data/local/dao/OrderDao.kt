package com.rmyfactory.inventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rmyfactory.inventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.inventorybarcode.model.data.local.model.with.OrderWithProducts

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: OrderModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orders: List<OrderModel>)

    @Query("SELECT * FROM order_table")
    suspend fun susReadOrders(): List<OrderModel>

    @Query("SELECT * FROM order_table WHERE id=:orderId")
    fun readOrderById(orderId: String): LiveData<OrderModel>

    @Transaction
    @Query("SELECT * FROM order_table")
    fun readOrderWithProducts(): LiveData<List<OrderWithProducts>>

}