package com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel

data class ItemWithOrders(

    @Embedded
    val item: ItemModel,
    @Relation(
        parentColumn = "item_id",
        entityColumn = "order_id",
        associateBy = Junction(OrderItemModel::class)
    )
    val orders: List<OrderModel>

)
