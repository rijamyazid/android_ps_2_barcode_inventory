package com.rmyfactory.rmyinventorybarcode.model.repository

import androidx.lifecycle.LiveData
import com.rmyfactory.rmyinventorybarcode.model.data.local.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.LocalDataSource
import javax.inject.Inject

class MainRepository
@Inject constructor(private val localDataSource: LocalDataSource) {

    fun insertItem(item: ItemModel) {
        localDataSource.insertItem(item)
    }

    fun readItems(): LiveData<List<ItemModel>> {
        return localDataSource.readItems()
    }

}