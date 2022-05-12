package com.rmyfactory.inventorybarcode.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.inventorybarcode.model.data.local.model.ProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.ProductUnitModel
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

//    val ProductUnitRemovedIds = mutableListOf<List<Long>>()
    val productUnitRemovedIds = mutableMapOf<Int, Long>()
    var firstInit = true

    fun insertProduct(product: ProductDetailHolder) = viewModelScope.launch(Dispatchers.IO) {

        insertProduct(
            ProductModel(
                productId = product.productId,
                productName = product.productName,
                productNote = product.productNote
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

        val productUnits = mutableListOf<ProductUnitModel>().apply {
            product.productUnit.forEachIndexed { index, _ ->
                this.add(
                    ProductUnitModel(
                        productId = product.productId,
                        unitId = product.productUnit[index],
                        stock = product.productStock[index],
                        price = product.productPrice[index],
                        increment = product.productIncrement[index]
                    )
                )
            }
        }
        if (productUnits.isNotEmpty()) insertProductUnits(productUnits)

    }

    fun updateProduct(product: ProductDetailHolder) = viewModelScope.launch(Dispatchers.IO) {
        updateProduct(
            ProductModel(
                productId = product.productId,
                productName = product.productName,
                productNote = product.productNote
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

        Log.d("RMYFACTORYX", "$productUnitRemovedIds")
        val ProductUnitsAdd = mutableListOf<ProductUnitModel>()
        if (productUnitRemovedIds.isNotEmpty()) {
            productUnitRemovedIds.forEach {
                deleteProductUnitById(it.value)
            }

            product.productUnit.forEachIndexed { index, _ ->
                var noUnitRemoved = true
                productUnitRemovedIds.forEach breaking@ {
                    if(index == it.key) {
                        noUnitRemoved = false
                        return@breaking
                    }
                }
                if(noUnitRemoved) {
                    ProductUnitsAdd.add(
                        ProductUnitModel(
                            productId = product.productId,
                            unitId = product.productUnit[index],
                            stock = product.productStock[index],
                            price = product.productPrice[index],
                            increment = product.productIncrement[index]
                        )
                    )
                }
            }
            productUnitRemovedIds.clear()
        } else {
            product.productUnit.forEachIndexed { index, _ ->
                ProductUnitsAdd.add(
                    ProductUnitModel(
                        productId = product.productId,
                        unitId = product.productUnit[index],
                        stock = product.productStock[index],
                        price = product.productPrice[index],
                        increment = product.productIncrement[index]
                    )
                )
            }
        }

        deleteProductUnitsByProductId(product.productId)
        insertProductUnits(ProductUnitsAdd)
    }

    fun deleteProduct(ProductId: String) = viewModelScope.launch(Dispatchers.IO) {
        deleteProductById(ProductId)
        deleteProductUnitsByProductId(ProductId)
    }

    // Product Model
    fun insertProduct(product: ProductModel) {
        repository.insertProduct(product)
    }

    fun updateProduct(product: ProductModel) {
        repository.updateProduct(product)
    }

    fun deleteProductById(ProductId: String) {
        repository.deleteProductById(ProductId)
    }

    fun readProductById(ProductId: String): LiveData<ProductModel> {
        return repository.readProductById(ProductId)
    }

    fun readProductByIdWithUnits(ProductId: String)
            : LiveData<ProductWithUnits> = repository.readProductByIdWithUnits(ProductId)

    // Unit Model
    fun insertUnit(unit: UnitModel) {
        repository.insertUnit(unit)
    }

    fun insertUnits(unitList: List<UnitModel>) {
        repository.insertUnits(unitList)
    }

    fun readUnits(): LiveData<List<UnitModel>> = repository.readUnits()

    // ProductUnitModel
    fun readProductByProductAndUnitId(ProductId: String, unitId: String)
            : ProductUnitModel? {
        return repository.readProductByProductAndUnitId(ProductId, unitId)
    }

    fun updateProductUnits(productUnitList: List<ProductUnitModel>) {
        repository.updateProductUnits(productUnitList)
    }

    fun insertProductUnit(productUnit: ProductUnitModel) {
        repository.insertProductUnit(productUnit)
    }

    fun insertProductUnits(productUnitList: List<ProductUnitModel>) {
        repository.insertProductUnits(productUnitList)
    }

    fun deleteProductUnitById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteProductUnitById(id)
    }

    fun deleteProductUnitsByProductId(ProductId: String) {
        repository.deleteProductUnitsByProductId(ProductId)
    }
}