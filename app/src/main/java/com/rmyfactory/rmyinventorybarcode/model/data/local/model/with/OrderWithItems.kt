package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel

data class OrderWithItems(

    @Embedded
    val order: OrderModel,

    @Relation(entity = OrderItemModel::class, parentColumn = "id", entityColumn = "order_id")
    val orderWithItems: List<OrderItemWithItem>

)
