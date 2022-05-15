package com.rmyfactory.inventorybarcode.model.data.local.model.relations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rmyfactory.inventorybarcode.model.data.local.model.BaseModel

@Entity(tableName = "product_unit_table")
data class ProductUnitModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "product_id", index = true)
    val productId: String,
    @ColumnInfo(name = "unit_id", index = true)
    val unitId: String,
    @ColumnInfo(name = "stock")
    var stock: Int,
    @ColumnInfo(name = "price")
    var price: String,
    @ColumnInfo(name = "increment")
    val increment: Float = 1.0F
): BaseModel