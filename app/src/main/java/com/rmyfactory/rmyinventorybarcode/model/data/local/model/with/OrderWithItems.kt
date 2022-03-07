package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel

data class OrderWithItems(

    @Embedded
    val order: OrderModel,

    @Relation(
        parentColumn = "id",
        entity = ItemModel::class,
        entityColumn = "id",
        associateBy = Junction(
            OrderItemModel::class,
            parentColumn = "order_id",
            entityColumn = "item_id"
        )
    )
    val items: List<ItemModel>

)
