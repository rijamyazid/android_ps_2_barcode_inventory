package com.rmyfactory.inventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Relation
import com.rmyfactory.inventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.ProductUnitModel

data class ProductWithUnits (

    @Embedded
    val product: ProductModel,
    @Relation(
        entity = ProductUnitModel::class,
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val productUnitList: List<ProductUnitWithUnit>
)