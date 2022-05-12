package com.rmyfactory.inventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Relation
import com.rmyfactory.inventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.OrderProductModel

data class OrderWithProducts(

    @Embedded
    val order: OrderModel,

    @Relation(entity = OrderProductModel::class, parentColumn = "id", entityColumn = "order_id")
    val orderWithProducts: List<OrderProductWithProduct>

)
