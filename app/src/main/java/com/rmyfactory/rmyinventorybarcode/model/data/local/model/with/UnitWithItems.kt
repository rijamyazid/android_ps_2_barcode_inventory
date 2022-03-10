package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel

data class UnitWithItems(

    @Embedded
    val unit: UnitModel,
    @Relation(
        entity = ItemModel::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            parentColumn = "unit_id",
            entityColumn = "item_id",
            value = ItemUnitModel::class
        )
    )
    val itemList: List<ItemModel>

)