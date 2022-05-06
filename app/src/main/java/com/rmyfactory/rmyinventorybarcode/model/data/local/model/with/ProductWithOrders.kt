package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderProductModel

data class ProductWithOrders(

    @Embedded
    val product: ProductModel,
    @Relation(
        parentColumn = "id",
        entity = OrderModel::class,
        entityColumn = "id",
        associateBy = Junction(
            OrderProductModel::class,
            parentColumn = "product_id",
            entityColumn = "order_id"
        )
    )
    val orders: List<OrderModel>

)
