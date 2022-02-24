package com.rmyfactory.rmyinventorybarcode.model.data.local

import androidx.lifecycle.LiveData
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.ItemDao
import javax.inject.Inject

class LocalDataSource
@Inject constructor(private val itemDao: ItemDao) {

    fun insertItem(item: ItemModel) {
        itemDao.insertItem(item)
    }

    fun readItems(): LiveData<List<ItemModel>> {
        return itemDao.readItems()
    }

}