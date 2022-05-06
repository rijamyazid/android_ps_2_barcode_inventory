package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderProductModel

data class OrderProductWithProduct(

    @Embedded
    val orderProductModel: OrderProductModel,

    @Relation(entity = ProductModel::class, parentColumn = "product_id", entityColumn = "id")
    val product: ProductModel

)