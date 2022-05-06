package com.rmyfactory.rmyinventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ProductWithOrders
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ProductWithUnits

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<ProductModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: ProductModel)

    @Update
    fun updateProduct(product: ProductModel)

    @Query("SELECT * FROM product_table WHERE id=:productId")
    fun readProduct(productId: String): LiveData<ProductModel>

    @Query("SELECT * FROM product_table")
    fun readProducts(): LiveData<List<ProductModel>>

    @Query("SELECT * FROM product_table")
    suspend fun _readProducts(): List<ProductModel>

    @Query("DELETE FROM product_table WHERE id=:productId")
    fun deleteProductById(productId: String)

    @Transaction
    @Query("SELECT * FROM product_table")
    fun readProductsWithOrders(): LiveData<List<ProductWithOrders>>

    @Transaction
    @Query("SELECT * FROM product_table")
    fun readProductWithUnits(): LiveData<List<ProductWithUnits>>

    @Transaction
    @Query("SELECT * FROM product_table WHERE product_name LIKE :query")
    fun readProductWithUnitsByQuery(query: String): LiveData<List<ProductWithUnits>>

    @Transaction
    @Query("SELECT * FROM product_table WHERE product_name LIKE :query")
    suspend fun susReadProductWithUnitsByQuery(query: String): List<ProductWithUnits>

    @Transaction
    @Query("SELECT * FROM product_table")
    suspend fun readProductWithUnits_(): List<ProductWithUnits>

    @Transaction
    @Query("SELECT * FROM product_table WHERE id=:productId")
    fun readProductByIdWithUnits(productId: String): LiveData<ProductWithUnits>

}