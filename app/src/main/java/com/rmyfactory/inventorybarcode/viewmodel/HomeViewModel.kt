package com.rmyfactory.inventorybarcode.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.inventorybarcode.model.data.local.model.BaseModel
import com.rmyfactory.inventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.inventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.ProductUnitModel
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
import com.rmyfactory.inventorybarcode.util.Constants
import com.rmyfactory.inventorybarcode.util.ResponseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(private val repository: MainRepository): ViewModel() {

    private suspend fun susReadProducts(): List<ProductModel> = repository.susReadProducts()
    private suspend fun susReadOrders(): List<OrderModel> = repository.susReadOrders()
    private suspend fun susReadUnits(): List<UnitModel> = repository.susReadUnits()
    private suspend fun susReadProductUnits(): List<ProductUnitModel> = repository.susReadProductUnits()
    private suspend fun susReadOrderProducts(): List<OrderProductModel> = repository.susReadOrderProducts()

    fun exportDataset(loadingProgress: (Int) -> Unit, loadingResult: (ResponseResult<String>) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val listOfProductModel = susReadProducts()
            var exportContent = "#${Constants.TABLE_PRODUCT}\n"
            listOfProductModel.forEach { Product ->
                exportContent += "${Product.productId};${Product.productName};${Product.productNote}\n"
            }
            loadingProgress(20)

            val listOfOrderModel = susReadOrders()
            exportContent += "\n#${Constants.TABLE_ORDER}\n"
            listOfOrderModel.forEach { order ->
                val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale("id", "ID"))
                exportContent += "${order.orderId};${order.orderPay};${order.orderExchange};${order.orderTotalPrice};${
                    sdf.format(
                        order.orderDate!!
                    )
                }\n"
            }
            loadingProgress(20)

            val listOfUnitModel = susReadUnits()
            exportContent += "\n#${Constants.TABLE_UNIT}\n"
            listOfUnitModel.forEach { unit ->
                exportContent += "${unit.unitId}\n"
            }
            loadingProgress(10)

            val listOfOrderProductModel = susReadOrderProducts()
            exportContent += "\n#${Constants.TABLE_ORDER_PRODUCT}\n"
            listOfOrderProductModel.forEach { orderProduct ->
                exportContent += "${orderProduct.orderId};${orderProduct.productId};${orderProduct.unit};${orderProduct.qty};${orderProduct.price};${orderProduct.totalPrice}\n"
            }
            loadingProgress(20)
            val listOfProductUnitModel = susReadProductUnits()
            exportContent += "\n#${Constants.TABLE_PRODUCT_UNIT}\n"
            listOfProductUnitModel.forEach { ProductUnit ->
                exportContent += "${ProductUnit.id};${ProductUnit.productId};${ProductUnit.unitId};${ProductUnit.stock};${ProductUnit.price}\n"
            }
            loadingProgress(30)

//            viewModelScope.launch(Dispatchers.Main) {
                loadingResult(ResponseResult.Success(exportContent))
//            }
        } catch (e: Exception) {
//            viewModelScope.launch(Dispatchers.Main) {
                loadingResult(ResponseResult.Failure(e))
//            }
        }
    }

    fun importDataset(inputStream: InputStream?, loadingProgress: (Int) -> Unit, loadingResult: (ResponseResult<Any>) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
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
                                        Log.d("Homee", "1")
                                        mapOfImportedData[currentTable]?.add(
                                            ProductModel(
                                                productId = lineSplit[0],
                                                productName = lineSplit[1],
                                                productNote = lineSplit[2]
                                            )
                                        )
                                    }
                                    Constants.TABLE_ORDER -> {
                                        Log.d("Homee", "2")
                                        val sdf = SimpleDateFormat(
                                            "dd MMM yyyy HH:mm:ss",
                                            Locale("id", "ID")
                                        )
                                        val dateFormatted = sdf.parse(lineSplit[4])
                                        mapOfImportedData[currentTable]?.add(
                                            OrderModel(
                                                orderId = lineSplit[0],
                                                orderPay = lineSplit[1],
                                                orderExchange = lineSplit[2],
                                                orderTotalPrice = lineSplit[3],
                                                orderDate = dateFormatted
                                            )
                                        )
                                    }
                                    Constants.TABLE_UNIT -> {
                                        Log.d("Homee", "3")
                                        mapOfImportedData[currentTable]?.add(
                                            UnitModel(
                                                unitId = lineSplit[0]
                                            )
                                        )
                                    }
                                    Constants.TABLE_ORDER_PRODUCT -> {
                                        Log.d("Homee", "4")
                                        mapOfImportedData[currentTable]?.add(
                                            OrderProductModel(
                                                orderId = lineSplit[0],
                                                productId = lineSplit[1],
                                                unit = lineSplit[2],
                                                qty = lineSplit[3].toInt(),
                                                price = lineSplit[4],
                                                totalPrice = lineSplit[5]
                                            )
                                        )
                                    }
                                    Constants.TABLE_PRODUCT_UNIT -> {
                                        Log.d("Homee", "5")
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
                loadingResult(ResponseResult.Success(Any()))
//            }
        } catch (e: Exception) {
            Log.d("Homee", "e: $e")
//            viewModelScope.launch(Dispatchers.Main) {
                loadingResult(ResponseResult.Failure(e))
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