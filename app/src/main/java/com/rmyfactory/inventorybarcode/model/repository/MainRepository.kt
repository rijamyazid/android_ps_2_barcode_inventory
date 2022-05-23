package com.rmyfactory.inventorybarcode.model.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
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

    suspend fun susReadProducts(): List<ProductModel> = localDataSource.susReadProducts()

    fun readProductById(ProductId: String)
            : LiveData<ProductModel> = localDataSource.readProductById(ProductId)

    fun readProductWithUnits(): LiveData<PagingData<ProductWithUnits>> {
        return Pager(
            config = PagingConfig(pageSize = 20)
        ) {
            localDataSource.readProductWithUnits()
        }.liveData
    }

    suspend fun readProductWithUnits_(): List<ProductWithUnits> =
        localDataSource.readProductWithUnits_()

    suspend fun susReadProductWithUnitsById(productId: String)
            : ProductWithUnits? = localDataSource.susReadProductWithUnitsById(productId)

    //Order Model
    fun insertOrder(order: OrderModel) {
        localDataSource.insertOrder(order)
    }

    fun insertOrders(orders: List<OrderModel>) {
        localDataSource.insertOrders(orders)
    }

    suspend fun susReadOrders(): List<OrderModel> = localDataSource.susReadOrders()

    fun readOrders(): LiveData<PagingData<OrderModel>>  {
        return Pager(config = PagingConfig(pageSize = 30)) {
            localDataSource.readOrders()
        }.liveData
    }

    fun readOrderWithProducts()
            : LiveData<PagingData<OrderWithProducts>> {
        return Pager(config = PagingConfig(pageSize = 30)) {
            localDataSource.readOrderWithProducts()
        }.liveData
    }

    suspend fun susReadOrderWithProductById(orderId: String): OrderWithProducts =
        localDataSource.susReadOrderWithProductById(orderId)

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

    fun readProductWithUnitsByQuery(query: String): LiveData<PagingData<ProductWithUnits>> {
        return Pager(
            config = PagingConfig(pageSize = 20)
        ) {
            localDataSource.readProductWithUnitsByQuery(query)
        }.liveData
    }

    suspend fun susReadProductWithUnitsByQuery(query: String)
            : List<ProductWithUnits> = localDataSource.susReadProductWithUnitsByQuery(query)

    suspend fun susReadUnits(): List<UnitModel> = localDataSource.susReadUnits()

    fun readUnitById(unitId: String)
            : UnitModel? = localDataSource.readUnitById(unitId)

    fun readUnitByNameWithProducts(unitName: String)
            : LiveData<UnitWithProducts> = localDataSource.readUnitByNameWithProducts(unitName)

    //OrderProduct Model
    fun insertOrderProducts(orders: List<OrderProductModel>) {
        localDataSource.insertOrderProducts(orders)
    }

    suspend fun susReadOrderProducts(): List<OrderProductModel> =
        localDataSource.susReadOrderProducts()

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

    suspend fun susReadProductUnits(): List<ProductUnitModel> =
        localDataSource.susReadProductUnits()

}