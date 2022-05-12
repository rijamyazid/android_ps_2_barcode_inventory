package com.rmyfactory.inventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rmyfactory.inventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.ProductUnitModel

data class UnitWithProducts(

    @Embedded
    val unit: UnitModel,
    @Relation(
        entity = ProductModel::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            parentColumn = "unit_id",
            entityColumn = "product_id",
            value = ProductUnitModel::class
        )
    )
    val productList: List<ProductModel>

)