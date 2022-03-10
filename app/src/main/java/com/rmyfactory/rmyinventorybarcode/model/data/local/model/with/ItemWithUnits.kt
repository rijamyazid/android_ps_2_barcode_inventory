package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel

data class ItemWithUnits (

    @Embedded
    val item: ItemModel,
    @Relation(
        entity = ItemUnitModel::class,
        parentColumn = "id",
        entityColumn = "item_id"
    )
    val itemUnitList: List<ItemUnitWithUnit>
)