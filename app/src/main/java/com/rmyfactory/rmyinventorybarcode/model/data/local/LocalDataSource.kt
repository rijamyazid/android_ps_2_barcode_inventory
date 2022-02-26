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

    fun readItemById(itemId: String): LiveData<ItemModel> {
        return itemDao.readItem(itemId)
    }

    fun deleteItemById(itemId: String) {
        itemDao.deleteItemById(itemId)
    }

    fun updateItem(item: ItemModel) {
        itemDao.updateItem(item)
    }

}