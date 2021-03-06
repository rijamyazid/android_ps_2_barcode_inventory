package com.rmyfactory.inventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rmyfactory.inventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.with.UnitWithProducts

@Dao
interface UnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUnit(unit: UnitModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUnits(units: List<UnitModel>)

    @Update
    fun updateUnit(unit: UnitModel)

    @Update
    fun updateUnits(units: List<UnitModel>)

    @Query("SELECT * FROM unit_table")
    fun readUnits(): LiveData<List<UnitModel>>

    @Query("SELECT * FROM unit_table")
    suspend fun susReadUnits(): List<UnitModel>

    @Query("SELECT * FROM unit_table WHERE id=:unitId")
    fun readUnitById(unitId: String): UnitModel?

    @Transaction
    @Query("SELECT * FROM unit_table WHERE id=:unitId")
    fun readUnitByIdWithProducts(unitId: String): LiveData<UnitWithProducts>

}