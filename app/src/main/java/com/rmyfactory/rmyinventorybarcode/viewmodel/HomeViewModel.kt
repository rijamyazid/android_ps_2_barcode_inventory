package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.BaseModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import com.rmyfactory.rmyinventorybarcode.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(private val repository: MainRepository): ViewModel() {

    private suspend fun _readItems(): List<ItemModel> = repository._readItems()
    private suspend fun _readOrders(): List<OrderModel> = repository._readOrders()
    private suspend fun _readUnits(): List<UnitModel> = repository._readUnits()
    private suspend fun _readItemUnits(): List<ItemUnitModel> = repository._readItemUnits()
    private suspend fun _readOrderItems(): List<OrderItemModel> = repository._readOrderItems()

    fun exportDataset(loadingProgress: (Int) -> Unit, datasetFetched: (String) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val listOfItemModel = _readItems()
        var exportContent = "#item_table\n"
        listOfItemModel.forEach { item ->
            exportContent += "${item.itemId};${item.itemName};${item.itemNote}\n"
        }
        loadingProgress(20)

        val listOfOrderModel = _readOrders()
        exportContent += "\n#order_table\n"
        listOfOrderModel.forEach { order ->
            exportContent += "${order.orderId};${order.orderPay};${order.orderExchange};${order.orderTotalPrice}\n"
        }
        loadingProgress(20)

        val listOfUnitModel = _readUnits()
        exportContent += "\n#unit_table\n"
        listOfUnitModel.forEach { unit ->
            exportContent += "${unit.unitId}\n"
        }
        loadingProgress(10)

        val listOfOrderItemModel = _readOrderItems()
        exportContent += "\n#order_item_table\n"
        listOfOrderItemModel.forEach { orderItem ->
            exportContent += "${orderItem.orderId};${orderItem.itemId};${orderItem.qty};${orderItem.price};${orderItem.totalPrice}\n"
        }
        loadingProgress(20)
        val listOfItemUnitModel = _readItemUnits()
        exportContent += "\n#item_unit_table\n"
        listOfItemUnitModel.forEach { itemUnit ->
            exportContent += "${itemUnit.id};${itemUnit.itemId};${itemUnit.unitId};${itemUnit.stock};${itemUnit.price}\n"
        }
        loadingProgress(30)

        datasetFetched(exportContent)
    }

    fun importDataset(inputStream: InputStream?, loadingProgress: (Int) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val mapOfImportedData = mutableMapOf<String, MutableList<BaseModel>>()
        var currentTable = "none"

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
                                Constants.TABLE_ITEM -> {
                                    mapOfImportedData[currentTable]?.add(
                                        ItemModel(
                                            itemId = lineSplit[0],
                                            itemName = lineSplit[1],
                                            itemNote = lineSplit[2]
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
                                Constants.TABLE_ORDER_ITEM -> {
                                    mapOfImportedData[currentTable]?.add(
                                        OrderItemModel(
                                            orderId = lineSplit[0],
                                            itemId = lineSplit[1],
                                            qty = lineSplit[2].toInt(),
                                            price = lineSplit[3],
                                            totalPrice = lineSplit[4]
                                        )
                                    )
                                }
                                Constants.TABLE_ITEM_UNIT -> {
                                    mapOfImportedData[currentTable]?.add(
                                        ItemUnitModel(
                                            id = lineSplit[0].toLong(),
                                            itemId = lineSplit[1],
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
    }

    private fun importToDatabase(mapOfImportedData: Map<String, List<BaseModel>>, loadingProgress: (Int) -> Unit) {
        val listOfItems = mapOfImportedData[Constants.TABLE_ITEM]
        val listOfOrders = mapOfImportedData[Constants.TABLE_ORDER]
        val listOfUnits = mapOfImportedData[Constants.TABLE_UNIT]
        val listOfOrderItems = mapOfImportedData[Constants.TABLE_ORDER_ITEM]
        val listOfItemUnits = mapOfImportedData[Constants.TABLE_ITEM_UNIT]

        listOfItems?.let {
            repository.insertItems(it.filterIsInstance<ItemModel>())
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

        listOfOrderItems?.let {
            repository.insertOrderItems(it.filterIsInstance<OrderItemModel>())
        }
        loadingProgress(10)

        listOfItemUnits?.let {
            repository.insertItemUnits(it.filterIsInstance<ItemUnitModel>())
        }
        loadingProgress(10)
    }
}