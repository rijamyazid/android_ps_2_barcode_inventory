package com.rmyfactory.inventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.OrderProductModel

@Dao
interface OrderProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderProducts(orderProducts: List<OrderProductModel>)

    @Query("SELECT * FROM order_product_table WHERE order_id=:orderId")
    fun readOrderProductsByOrderId(orderId: String): LiveData<List<OrderProductModel>>

    @Query("SELECT * FROM order_product_table")
    fun readOrderProducts(): LiveData<List<OrderProductModel>>

    @Query("SELECT * FROM order_product_table")
    suspend fun susReadOrderProducts(): List<OrderProductModel>

}