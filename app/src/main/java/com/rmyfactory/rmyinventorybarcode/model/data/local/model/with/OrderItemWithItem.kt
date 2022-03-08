package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel

data class OrderItemWithItem(

    @Embedded
    val orderItemModel: OrderItemModel,

    @Relation(entity = ItemModel::class, parentColumn = "item_id", entityColumn = "id")
    val item: ItemModel

)