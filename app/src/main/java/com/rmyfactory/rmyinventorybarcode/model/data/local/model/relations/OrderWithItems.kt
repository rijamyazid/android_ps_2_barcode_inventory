package com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel

data class OrderWithItems(

    @Embedded
    val order: OrderModel,

    @Relation(
        parentColumn = "order_id",
        entityColumn = "item_id",
        associateBy = Junction(OrderItemModel::class)
    )
    val items: List<ItemModel>

)
