package com.rmyfactory.inventorybarcode.model.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unit_table")
data class UnitModel(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val unitId: String,

): BaseModel