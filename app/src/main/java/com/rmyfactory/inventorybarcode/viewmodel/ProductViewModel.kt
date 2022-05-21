package com.rmyfactory.inventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.paging.PagingData
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val productQuery = MutableLiveData<String>()
    val productWithUnitsByQuery = productQuery.switchMap {
        if (it.isEmpty()) {
            repository.readProductWithUnits()
//            repository.readProductWithUnits()
        } else {
            repository.readProductWithUnitsByQuery("$it%")
//            repository.readProductWithUnitsByQuery("$it%")
        }
    }

    fun updateProductQuery(query: String) {
        productQuery.value = query
    }

    fun readProductWithUnits(): LiveData<PagingData<ProductWithUnits>> {
        return repository.readProductWithUnits()
    }

}