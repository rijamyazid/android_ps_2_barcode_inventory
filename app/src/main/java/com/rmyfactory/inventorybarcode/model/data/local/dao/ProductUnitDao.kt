package com.rmyfactory.inventorybarcode.model.data.local.dao

import androidx.room.*
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.ProductUnitModel

@Dao
interface ProductUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductUnit(productUnit: ProductUnitModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductUnits(productUnits: List<ProductUnitModel>)

    @Update
    fun updateProductUnits(productUnits: List<ProductUnitModel>)

    @Query("DELETE FROM product_unit_table WHERE id=:id")
    fun deleteProductUnitById(id: Long)

    @Query("DELETE FROM product_unit_table WHERE product_id=:productId")
    fun deleteProductUnitsByProductId(productId: String)

    @Query("SELECT * FROM product_unit_table")
    suspend fun _readProductUnits(): List<ProductUnitModel>

    @Query("SELECT * FROM product_unit_table WHERE product_id=:productId AND unit_id=:unitId")
    fun readProductByProductAndUnitId(productId: String, unitId: String): ProductUnitModel?

}