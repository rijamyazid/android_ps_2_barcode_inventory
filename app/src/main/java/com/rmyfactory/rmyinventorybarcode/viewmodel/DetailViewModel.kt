package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    fun insertProduct(product: ProductDetailHolder) = viewModelScope.launch(Dispatchers.IO) {

        insertItem(
            ItemModel(
                itemId = product.productId,
                itemName = product.productName,
                itemNote = product.productNote
            )
        )

        val units = mutableListOf<UnitModel>()
        product.productUnit.forEach {
            if (repository.readUnitById(it) == null) {
                units.add(
                    UnitModel(unitId = it)
                )
            }
        }
        if (units.isNotEmpty()) insertUnits(units)

        val itemUnits = mutableListOf<ItemUnitModel>().apply {
            product.productUnit.forEachIndexed { index, _ ->
                this.add(
                    ItemUnitModel(
                        itemId = product.productId,
                        unitId = product.productUnit[index],
                        stock = product.productStock[index],
                        price = product.productPrice[index]
                    )
                )
            }
        }
        if (itemUnits.isNotEmpty()) insertItemUnits(itemUnits)

    }

    fun updateProduct(product: ProductDetailHolder) = viewModelScope.launch(Dispatchers.IO) {
        updateItem(
            ItemModel(
                itemId = product.productId,
                itemName = product.productName,
                itemNote = product.productNote
            )
        )

        val units = mutableListOf<UnitModel>()
        product.productUnit.forEach {
            if (repository.readUnitById(it) == null) {
                units.add(
                    UnitModel(unitId = it)
                )
            }
        }
        if (units.isNotEmpty()) insertUnits(units)

        val itemUnitsAdd = mutableListOf<ItemUnitModel>()
        product.productUnit.forEachIndexed { index, _ ->
                itemUnitsAdd.add(
                    ItemUnitModel(
                        itemId = product.productId,
                        unitId = product.productUnit[index],
                        stock = product.productStock[index],
                        price = product.productPrice[index]
                    )
                )
        }

        deleteItemUnitsByItemId(product.productId)
        insertItemUnits(itemUnitsAdd)
    }

    fun deleteProduct(itemId: String) = viewModelScope.launch(Dispatchers.IO) {
        deleteItemById(itemId)
        deleteItemUnitsByItemId(itemId)
    }

    // Item Model
    fun insertItem(item: ItemModel) {
        repository.insertItem(item)
    }

    fun updateItem(item: ItemModel) {
        repository.updateItem(item)
    }

    fun deleteItemById(itemId: String) {
        repository.deleteItemById(itemId)
    }

    fun readItemById(itemId: String): LiveData<ItemModel> {
        return repository.readItemById(itemId)
    }

    fun readItemByIdWithUnits(itemId: String)
            : LiveData<ItemWithUnits> = repository.readItemByIdWithUnits(itemId)

    // Unit Model
    fun insertUnit(unit: UnitModel) {
        repository.insertUnit(unit)
    }

    fun insertUnits(unitList: List<UnitModel>) {
        repository.insertUnits(unitList)
    }

    fun readUnits(): LiveData<List<UnitModel>> = repository.readUnits()

    // ItemUnitModel
    fun readItemByItemAndUnitId(itemId: String, unitId: String)
    : ItemUnitModel? {
        return repository.readItemByItemAndUnitId(itemId, unitId)
    }
    fun updateItemUnits(itemUnitList: List<ItemUnitModel>) {
        repository.updateItemUnits(itemUnitList)
    }

    fun insertItemUnit(itemUnit: ItemUnitModel) {
        repository.insertItemUnit(itemUnit)
    }

    fun insertItemUnits(itemUnitList: List<ItemUnitModel>) {
        repository.insertItemUnits(itemUnitList)
    }

    fun deleteItemUnitsByItemId(itemId: String) {
        repository.deleteItemUnitsByItemId(itemId)
    }
}