package com.rmyfactory.inventorybarcode.model.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.rmyfactory.inventorybarcode.model.data.local.dao.*
import com.rmyfactory.inventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.inventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.ProductUnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.with.OrderWithProducts
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import com.rmyfactory.inventorybarcode.model.data.local.model.with.UnitWithProducts
import javax.inject.Inject

class LocalDataSource
@Inject constructor(
    private val productDao: ProductDao,
    private val orderDao: OrderDao,
    private val orderProductDao: OrderProductDao,
    private val unitDao: UnitDao,
    private val productUnitDao: ProductUnitDao
) {

    //Product Model
    fun insertProduct(product: ProductModel) {
        productDao.insertProduct(product)
    }

    fun insertProducts(products: List<ProductModel>) {
        productDao.insertProducts(products)
    }

    fun updateProduct(product: ProductModel) {
        productDao.updateProduct(product)
    }

    fun deleteProductById(ProductId: String) {
        productDao.deleteProductById(ProductId)
    }

    fun readProducts()
            : LiveData<List<ProductModel>> = productDao.readProducts()

    fun readProductWithUnitsByQuery(query: String): LiveData<List<ProductWithUnits>> =
        productDao.readProductWithUnitsByQuery(query)

    suspend fun susReadProductWithUnitsByQuery(query: String)
            : List<ProductWithUnits> = productDao.susReadProductWithUnitsByQuery(query)

    suspend fun susReadProducts(): List<ProductModel> = productDao.susReadProducts()

    fun readProductById(ProductId: String)
            : LiveData<ProductModel> = productDao.readProduct(ProductId)

    fun readProductWithUnits()
            : LiveData<List<ProductWithUnits>> = productDao.readProductWithUnits()

    suspend fun readProductWithUnits_(): List<ProductWithUnits> = productDao.susReadProductWithUnits()

    suspend fun susReadProductWithUnitsById(productId: String)
            : ProductWithUnits? = productDao.susReadProductWithUnitsById(productId)

    //Order Model
    fun insertOrder(order: OrderModel) {
        orderDao.insertOrder(order)
    }

    fun insertOrders(orders: List<OrderModel>) {
        orderDao.insertOrders(orders)
    }

    suspend fun susReadOrders(): List<OrderModel> = orderDao.susReadOrders()

    fun readOrders(): PagingSource<Int, OrderModel> = orderDao.readOrders()

    fun readOrderWithProducts()
            : PagingSource<Int, OrderWithProducts> = orderDao.readOrderWithProducts()

    suspend fun susReadOrderWithProductById(orderId: String): OrderWithProducts?
    = orderDao.susReadOrderWithProductById(orderId)

    //Unit Model
    fun insertUnit(unit: UnitModel) {
        unitDao.insertUnit(unit)
    }

    fun insertUnits(unitList: List<UnitModel>) {
        unitDao.insertUnits(unitList)
    }

    fun updateUnits(unitList: List<UnitModel>) {
        unitDao.updateUnits(unitList)
    }

    fun readUnits()
            : LiveData<List<UnitModel>> = unitDao.readUnits()

    suspend fun susReadUnits(): List<UnitModel> = unitDao.susReadUnits()

    fun readUnitById(unitId: String)
            : UnitModel? = unitDao.readUnitById(unitId)

    fun readUnitByNameWithProducts(unitId: String)
            : LiveData<UnitWithProducts> = unitDao.readUnitByIdWithProducts(unitId = unitId)

    //OrderProduct Model
    fun insertOrderProducts(orderList: List<OrderProductModel>) {
        orderProductDao.insertOrderProducts(orderList)
    }

    suspend fun susReadOrderProducts(): List<OrderProductModel> = orderProductDao.susReadOrderProducts()

    //ProductUnit Model
    fun insertProductUnit(productUnit: ProductUnitModel) {
        productUnitDao.insertProductUnit(productUnit)
    }

    fun insertProductUnits(productUnitList: List<ProductUnitModel>) {
        productUnitDao.insertProductUnits(productUnitList)
    }

    fun updateProductUnits(productUnitList: List<ProductUnitModel>) {
        productUnitDao.updateProductUnits(productUnitList)
    }

    fun deleteProductUnitById(id: Long) {
        productUnitDao.deleteProductUnitById(id)
    }

    fun deleteProductUnitsByProductId(ProductId: String) {
        productUnitDao.deleteProductUnitsByProductId(ProductId)
    }

    suspend fun susReadProductUnits(): List<ProductUnitModel> = productUnitDao.susReadProductUnits()

    fun readProductByProductAndUnitId(ProductId: String, unitId: String): ProductUnitModel? =
        productUnitDao.readProductByProductAndUnitId(ProductId, unitId)

}