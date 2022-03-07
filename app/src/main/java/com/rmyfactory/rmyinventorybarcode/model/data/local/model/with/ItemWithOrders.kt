package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel

data class ItemWithOrders(

    @Embedded
    val item: ItemModel,
    @Relation(
        parentColumn = "id",
        entity = OrderModel::class,
        entityColumn = "id",
        associateBy = Junction(
            OrderItemModel::class,
            parentColumn = "item_id",
            entityColumn = "order_id"
        )
    )
    val orders: List<OrderModel>

)
