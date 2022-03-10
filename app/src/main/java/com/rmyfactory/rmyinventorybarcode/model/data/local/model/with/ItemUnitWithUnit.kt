package com.rmyfactory.rmyinventorybarcode.model.data.local.model.with

import androidx.room.Embedded
import androidx.room.Relation
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel

data class ItemUnitWithUnit(

    @Embedded
    val itemUnit: ItemUnitModel,
    @Relation(
        entity = UnitModel::class,
        parentColumn = "unit_id",
        entityColumn = "id"
    )
    val unit: UnitModel

)
