package com.rmyfactory.inventorybarcode.model.repository

import androidx.lifecycle.LiveData
import com.rmyfactory.inventorybarcode.model.data.local.LocalDataSource
import com.rmyfactory.inventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.inventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.ProductUnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.with.OrderWithProducts
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import com.rmyfactory.inventorybarcode.model.data.local.model.with.UnitWithProducts
import javax.inject.Inject

class MainRepository
@Inject constructor(private val localDataSource: LocalDataSource) {

    //Product Model
    fun insertProduct(product: ProductModel) {
        localDataSource.insertProduct(product)
    }

    fun insertProducts(products: List<ProductModel>) {
        localDataSource.insertProducts(products)
    }

    fun updateProduct(product: ProductModel) {
        localDataSource.updateProduct(product)
    }

    fun deleteProductById(ProductId: String) {
        localDataSource.deleteProductById(ProductId)
    }

    fun readProducts()
            : LiveData<List<ProductModel>> = localDataSource.readProducts()

    suspend fun _readProducts(): List<ProductModel> = localDataSource._readProducts()

    fun readProductById(ProductId: String)
            : LiveData<ProductModel> = localDataSource.readProductById(ProductId)

    fun readProductWithUnits()
            : LiveData<List<ProductWithUnits>> = localDataSource.readProductWithUnits()

    suspend fun readProductWithUnits_(): List<ProductWithUnits> = localDataSource.readProductWithUnits_()

    suspend fun susReadProductWithUnitsById(productId: String)
            : ProductWithUnits? = localDataSource.susReadProductWithUnitsById(productId)

    //Order Model
    fun insertOrder(order: OrderModel) {
        localDataSource.insertOrder(order)
    }

    fun insertOrders(orders: List<OrderModel>) {
        localDataSource.insertOrders(orders)
    }

    fun readOrderWithProducts()
            : LiveData<List<OrderWithProducts>> = localDataSource.readOrderWithProducts()

    suspend fun _readOrders(): List<OrderModel> = localDataSource._readOrders()

    //Unit Model
    fun insertUnit(unit: UnitModel) {
        localDataSource.insertUnit(unit)
    }

    fun insertUnits(unitList: List<UnitModel>) {
        localDataSource.insertUnits(unitList)
    }

    fun updateUnits(unitList: List<UnitModel>) {
        localDataSource.updateUnits(unitList)
    }

    fun readUnits()
            : LiveData<List<UnitModel>> = localDataSource.readUnits()

    fun readProductWithUnitsByQuery(query: String): LiveData<List<ProductWithUnits>>
    = localDataSource.readProductWithUnitsByQuery(query)

    suspend fun susReadProductWithUnitsByQuery(query: String)
            : List<ProductWithUnits> = localDataSource.susReadProductWithUnitsByQuery(query)

    suspend fun _readUnits(): List<UnitModel> = localDataSource._readUnits()

    fun readUnitById(unitId: String)
            : UnitModel? = localDataSource.readUnitById(unitId)

    fun readUnitByNameWithProducts(unitName: String)
            : LiveData<UnitWithProducts> = localDataSource.readUnitByNameWithProducts(unitName)

    //OrderProduct Model
    fun insertOrderProducts(orders: List<OrderProductModel>) {
        localDataSource.insertOrderProducts(orders)
    }

    suspend fun _readOrderProducts(): List<OrderProductModel> = localDataSource._readOrderProducts()

    //ProductUnitModel
    fun readProductByProductAndUnitId(ProductId: String, unitId: String)
            : ProductUnitModel? = localDataSource.readProductByProductAndUnitId(ProductId, unitId)

    fun insertProductUnit(productUnit: ProductUnitModel) {
        localDataSource.insertProductUnit(productUnit)
    }

    fun insertProductUnits(productUnitList: List<ProductUnitModel>) {
        localDataSource.insertProductUnits(productUnitList)
    }

    fun updateProductUnits(productUnitList: List<ProductUnitModel>) {
        localDataSource.updateProductUnits(productUnitList)
    }

    fun deleteProductUnitById(id: Long) {
        localDataSource.deleteProductUnitById(id)
    }

    fun deleteProductUnitsByProductId(ProductId: String) {
        localDataSource.deleteProductUnitsByProductId(ProductId)
    }

    suspend fun _readProductUnits(): List<ProductUnitModel> = localDataSource._readProductUnits()

}