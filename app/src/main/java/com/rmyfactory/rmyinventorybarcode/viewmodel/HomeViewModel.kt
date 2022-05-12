package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.BaseModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ProductUnitModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import com.rmyfactory.rmyinventorybarcode.util.Constants
import com.rmyfactory.rmyinventorybarcode.util.ResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(private val repository: MainRepository): ViewModel() {

    private suspend fun _readProducts(): List<ProductModel> = repository._readProducts()
    private suspend fun _readOrders(): List<OrderModel> = repository._readOrders()
    private suspend fun _readUnits(): List<UnitModel> = repository._readUnits()
    private suspend fun _readProductUnits(): List<ProductUnitModel> = repository._readProductUnits()
    private suspend fun _readOrderProducts(): List<OrderProductModel> = repository._readOrderProducts()

    fun exportDataset(loadingProgress: (Int) -> Unit, loadingResult: (ResultResponse<String>) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val listOfProductModel = _readProducts()

        try {
            var exportContent = "#${Constants.TABLE_PRODUCT}\n"
            listOfProductModel.forEach { Product ->
                exportContent += "${Product.productId};${Product.productName};${Product.productNote}\n"
            }
            loadingProgress(20)

            val listOfOrderModel = _readOrders()
            exportContent += "\n#${Constants.TABLE_ORDER}\n"
            listOfOrderModel.forEach { order ->
                exportContent += "${order.orderId};${order.orderPay};${order.orderExchange};${order.orderTotalPrice}\n"
            }
            loadingProgress(20)

            val listOfUnitModel = _readUnits()
            exportContent += "\n#${Constants.TABLE_UNIT}\n"
            listOfUnitModel.forEach { unit ->
                exportContent += "${unit.unitId}\n"
            }
            loadingProgress(10)

            val listOfOrderProductModel = _readOrderProducts()
            exportContent += "\n#${Constants.TABLE_ORDER_PRODUCT}\n"
            listOfOrderProductModel.forEach { orderProduct ->
                exportContent += "${orderProduct.orderId};${orderProduct.productId};${orderProduct.qty};${orderProduct.price};${orderProduct.totalPrice}\n"
            }
            loadingProgress(20)
            val listOfProductUnitModel = _readProductUnits()
            exportContent += "\n#${Constants.TABLE_PRODUCT_UNIT}\n"
            listOfProductUnitModel.forEach { ProductUnit ->
                exportContent += "${ProductUnit.id};${ProductUnit.productId};${ProductUnit.unitId};${ProductUnit.stock};${ProductUnit.price}\n"
            }
            loadingProgress(30)

//            viewModelScope.launch(Dispatchers.Main) {
                loadingResult(ResultResponse.Success(exportContent))
//            }
        } catch (e: Exception) {
//            viewModelScope.launch(Dispatchers.Main) {
                loadingResult(ResultResponse.Failure(e))
//            }
        }
    }

    fun importDataset(inputStream: InputStream?, loadingProgress: (Int) -> Unit, loadingResult: (ResultResponse<Any>) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val mapOfImportedData = mutableMapOf<String, MutableList<BaseModel>>()
        var currentTable = "none"

        try {
            inputStream.use { input ->
                input?.reader()?.forEachLine { line ->
                    if (line.isNotEmpty()) {
                        if (line.first() == '#') {
                            loadingProgress(10)
                            currentTable = line.substring(1)
                            mapOfImportedData[currentTable] = mutableListOf()
                        } else {
                            val lineSplit = line.split(";")
                            if (lineSplit.isNotEmpty()) {
                                when (currentTable) {
                                    Constants.TABLE_PRODUCT -> {
                                        mapOfImportedData[currentTable]?.add(
                                            ProductModel(
                                                productId = lineSplit[0],
                                                productName = lineSplit[1],
                                                productNote = lineSplit[2]
                                            )
                                        )
                                    }
                                    Constants.TABLE_ORDER -> {
                                        mapOfImportedData[currentTable]?.add(
                                            OrderModel(
                                                orderId = lineSplit[0],
                                                orderPay = lineSplit[1],
                                                orderExchange = lineSplit[2],
                                                orderTotalPrice = lineSplit[3]
                                            )
                                        )
                                    }
                                    Constants.TABLE_UNIT -> {
                                        mapOfImportedData[currentTable]?.add(
                                            UnitModel(
                                                unitId = lineSplit[0]
                                            )
                                        )
                                    }
                                    Constants.TABLE_ORDER_PRODUCT -> {
                                        mapOfImportedData[currentTable]?.add(
                                            OrderProductModel(
                                                orderId = lineSplit[0],
                                                productId = lineSplit[1],
                                                qty = lineSplit[2].toInt(),
                                                price = lineSplit[3],
                                                totalPrice = lineSplit[4]
                                            )
                                        )
                                    }
                                    Constants.TABLE_PRODUCT_UNIT -> {
                                        mapOfImportedData[currentTable]?.add(
                                            ProductUnitModel(
                                                id = lineSplit[0].toLong(),
                                                productId = lineSplit[1],
                                                unitId = lineSplit[2],
                                                stock = lineSplit[3].toInt(),
                                                price = lineSplit[4]
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            importToDatabase(mapOfImportedData) { progress ->
                loadingProgress(progress)
            }
//            viewModelScope.launch(Dispatchers.Main) {
                loadingResult(ResultResponse.Success(Any()))
//            }
        } catch (e: Exception) {
//            viewModelScope.launch(Dispatchers.Main) {
                loadingResult(ResultResponse.Failure(e))
//            }
        }
    }

    private fun importToDatabase(mapOfImportedData: Map<String, List<BaseModel>>, loadingProgress: (Int) -> Unit) {
        val listOfProducts = mapOfImportedData[Constants.TABLE_PRODUCT]
        val listOfOrders = mapOfImportedData[Constants.TABLE_ORDER]
        val listOfUnits = mapOfImportedData[Constants.TABLE_UNIT]
        val listOfOrderProducts = mapOfImportedData[Constants.TABLE_ORDER_PRODUCT]
        val listOfProductUnits = mapOfImportedData[Constants.TABLE_PRODUCT_UNIT]

        listOfProducts?.let {
            repository.insertProducts(it.filterIsInstance<ProductModel>())
        }
        loadingProgress(10)

        listOfOrders?.let {
            repository.insertOrders(it.filterIsInstance<OrderModel>())
        }
        loadingProgress(10)

        listOfUnits?.let {
            repository.insertUnits(it.filterIsInstance<UnitModel>())
        }
        loadingProgress(10)

        listOfOrderProducts?.let {
            repository.insertOrderProducts(it.filterIsInstance<OrderProductModel>())
        }
        loadingProgress(10)

        listOfProductUnits?.let {
            repository.insertProductUnits(it.filterIsInstance<ProductUnitModel>())
        }
        loadingProgress(10)
    }
}