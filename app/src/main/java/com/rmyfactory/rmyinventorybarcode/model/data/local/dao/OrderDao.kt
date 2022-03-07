package com.rmyfactory.rmyinventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrder(order: OrderModel)

    @Query("SELECT * FROM order_table WHERE id=:orderId")
    fun readOrderById(orderId: String): LiveData<OrderModel>

}