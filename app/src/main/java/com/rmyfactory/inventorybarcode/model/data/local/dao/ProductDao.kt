package com.rmyfactory.inventorybarcode.model.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.rmyfactory.inventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithOrders
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits

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
    suspend fun susReadProducts(): List<ProductModel>

    @Query("DELETE FROM product_table WHERE id=:productId")
    fun deleteProductById(productId: String)

    @Transaction
    @Query("SELECT * FROM product_table")
    fun readProductsWithOrders(): LiveData<List<ProductWithOrders>>

    @Transaction
    @Query("SELECT * FROM product_table")
    fun readProductWithUnits(): PagingSource<Int, ProductWithUnits>

    @Transaction
    @Query("SELECT * FROM product_table WHERE product_name LIKE :query")
    fun readProductWithUnitsByQuery(query: String): PagingSource<Int, ProductWithUnits>

    @Transaction
    @Query("SELECT * FROM product_table WHERE id=:productId")
    suspend fun susReadProductWithUnitsById(productId: String): ProductWithUnits?

    @Transaction
    @Query("SELECT * FROM product_table WHERE product_name LIKE :query")
    suspend fun susReadProductWithUnitsByQuery(query: String): List<ProductWithUnits>

    @Transaction
    @Query("SELECT * FROM product_table")
    suspend fun susReadProductWithUnits(): List<ProductWithUnits>

}